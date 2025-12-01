package com.kevinfreyap.core.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.source.local.entity.CartEntity
import com.kevinfreyap.core.data.source.local.room.CartDao
import com.kevinfreyap.core.data.source.remote.network.ApiService
import com.kevinfreyap.core.data.source.remote.network.AvailabilityStatus
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.model.cart.SimpleCartItem
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.repository.ICartRepository
import com.kevinfreyap.core.utils.Constants.CART_SUB_COLLECTION
import com.kevinfreyap.core.utils.Constants.FIELD_QUANTITY
import com.kevinfreyap.core.utils.Constants.USER_COLLECTION
import com.kevinfreyap.core.utils.DataMapper
import com.kevinfreyap.core.utils.getAuthUidFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val apiService: ApiService,
    private val cartDao: CartDao,
): ICartRepository {
    override fun getCartItems(): Flow<Resource<List<Cart>>>  {
        return cartDao.getAllCartItems().map { entities ->
            val domainList = entities.map { entity ->
                Cart(
                    product = DataMapper.mapCartEntityToDomain(entity),
                    quantity = entity.quantity,
                    isAvailable = entity.isAvailable
                )
            }
            Resource.Success(domainList)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCartItemCount(): Flow<Int> {
        return firebaseAuth.getAuthUidFlow()
            .flatMapLatest { userid ->
                if (userid.isNullOrEmpty()) {
                    flowOf(0)
                } else {
                    cartDao.getCartItemCount()
                }
            }
    }

    override fun addToCart(product: Product, quantity: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()){
            emit(Resource.Error("ERROR_USER_NOT_FOUND"))
            return@flow
        }

        val productExist = cartDao.getCartItemById(product.id)
        val insertQuantity = if (productExist != null) {
            productExist.quantity + quantity
        } else {
            quantity
        }

        val timestamp = System.currentTimeMillis()

        val cartEntity = CartEntity(
            productId = product.id,
            name = product.title,
            price = product.price,
            imageUrl = product.images.firstOrNull() ?: "",
            quantity = insertQuantity,
            isAvailable = true,
            dateAdded = timestamp
        )

        try {
            cartDao.insert(cartEntity)

            emit(Resource.Success(true))

            syncToFirestore(cartEntity, timestamp)
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "ERROR_FAILED_TO_ADD_TO_CART"))
        }
    }.flowOn(Dispatchers.IO)

    override fun updateItemQuantity(
        productId: String,
        newQuantity: Int
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()) {
            emit(Resource.Error("ERROR_USER_NOT_FOUND"))
            return@flow
        }

        try {
            cartDao.updateQuantity(productId, newQuantity)

            emit(Resource.Success(true))

            firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(CART_SUB_COLLECTION)
                .document(productId)
                .update(FIELD_QUANTITY, newQuantity)
                .addOnFailureListener {
                    Log.e("CartRepo", "Failed to sync update: ${it.message}")
                }

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Update Failed"))
        }
    }.flowOn(Dispatchers.IO)

    override fun removeItemFromCart(productId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()) {
            emit(Resource.Error("ERROR_USER_NOT_FOUND"))
            return@flow
        }

        try {
            cartDao.deleteItem(productId)

            emit(Resource.Success(true))

            firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(CART_SUB_COLLECTION)
                .document(productId)
                .delete()
                .addOnFailureListener {
                    Log.e("CartRepo", "Failed delete failed: ${it.message}")
                }

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Delete Failed"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun syncCartOnLogin() = withContext(Dispatchers.IO) {
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()) return@withContext

        try {
            val snapshot = firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(CART_SUB_COLLECTION)
                .get()
                .await()

            val simpleItems = snapshot.toObjects<SimpleCartItem>()
            if (simpleItems.isEmpty()) {
                cartDao.clearCart()
                return@withContext
            }

            // Check ProductId against API
            val productIds = simpleItems.map { it.productId }
            val availabilityMap = verifyProductsAvailability(productIds)

            val entitiesToInsert = simpleItems.map { simpleCartItem ->
                val status = availabilityMap[simpleCartItem.productId]

                // Set default values from Firestore (in case of offline/unknown)
                var name = simpleCartItem.title ?: ""
                var price = simpleCartItem.price ?: 0
                var imageUrl = simpleCartItem.imageUrl ?: ""
                val isAvailable: Boolean

                when(status) {
                    is AvailabilityStatus.Available -> {
                        val product = status.product
                        name = product.title
                        price = product.price
                        imageUrl = product.images.firstOrNull() ?: ""
                        isAvailable = true
                    }
                    is AvailabilityStatus.Unavailable -> {
                        isAvailable = false
                    }
                    is AvailabilityStatus.Unknown, null -> {
                        isAvailable = true
                    }
                }
                CartEntity(
                    productId = simpleCartItem.productId.toString(),
                    name = name,
                    price = price,
                    imageUrl = imageUrl,
                    quantity = simpleCartItem.quantity,
                    isAvailable = isAvailable,
                    dateAdded = simpleCartItem.dateAdded ?: System.currentTimeMillis()
                )
            }

            cartDao.clearCart()
            cartDao.insertAll(entitiesToInsert)
            Log.d("CartRepo", "Successfully synced cart from Firestore.")
        } catch (e: Exception) {
            Log.e("CartRepo", "Failed to sync cart on login: ${e.message}")
        }
    }

    override suspend fun clearCart() = withContext(Dispatchers.IO) {
        cartDao.clearCart()
    }

    override suspend fun clearFirestoreCart() = withContext(Dispatchers.IO) {
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()) return@withContext

        try {
            val collectionRef = firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(CART_SUB_COLLECTION)

            val snapshot = collectionRef.get().await()

            if (snapshot.isEmpty) {
                Log.d("CartRepo", "Firestore cart is already empty.")
                return@withContext // Nothing to delete
            }

            val batch = firestore.batch()
            for (document in snapshot.documents){
                batch.delete(document.reference)
            }

            batch.commit().await()
            Log.d("CartRepo", "Successfully cleared Firestore cart sub-collection.")
        } catch (e: Exception) {
            Log.e("CartRepo", "Failed to clear Firestore cart: ${e.message}")
        }
    }

    override fun refreshCartAvailability(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        val localItems = cartDao.getAllCartItems().first()
        if (localItems.isEmpty()) {
            emit(Resource.Success(true))
            return@flow
        }

        val productIds = localItems.map { it.productId.toInt() }
        val availabilityMap = verifyProductsAvailability(productIds)

        var isCartStillValid = true
        try {
            for (item in localItems) {
                val status = availabilityMap[item.productId.toInt()]

                when(status) {
                    is AvailabilityStatus.Available -> {
                        val product = status.product
                        val updatedEntity = item.copy(
                            name = product.title,
                            price = product.price,
                            imageUrl = product.images.firstOrNull() ?: "",
                            isAvailable = true
                        )
                        cartDao.update(updatedEntity)
                    }
                    is AvailabilityStatus.Unavailable -> {
                        isCartStillValid = false
                        if (item.isAvailable) {
                            cartDao.markAsUnavailable(item.productId)
                        }
                    }
                    is AvailabilityStatus.Unknown, null -> {
                        if (!item.isAvailable) {
                            isCartStillValid = false
                        }
                    }
                }
            }
            emit(Resource.Success(isCartStillValid))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to validate cart"))
        }
    }.flowOn(Dispatchers.IO)

    private fun syncToFirestore(cartEntity: CartEntity, timestamp: Long) {
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()) return

        val cartSimpleItem = SimpleCartItem(
            productId = cartEntity.productId.toInt(),
            quantity = cartEntity.quantity,
            title = cartEntity.name,
            price = cartEntity.price,
            imageUrl = cartEntity.imageUrl,
            dateAdded = timestamp
        )

        firestore.collection(USER_COLLECTION)
            .document(currentUserUid)
            .collection(CART_SUB_COLLECTION)
            .document(cartEntity.productId)
            .set(cartSimpleItem)
            .addOnSuccessListener {
                Log.d("CartRepo", "Successfully synced item ${cartEntity.productId}")
            }
            .addOnFailureListener {
                Log.e("CartRepo", "Failed to sync item ${cartEntity.productId}: ${it.message}")
            }
    }

    private suspend fun verifyProductsAvailability(productIds: List<Int>): Map<Int, AvailabilityStatus> = coroutineScope {
        val checks = productIds.map { id ->
            async(Dispatchers.IO) {
                try {
                    // Try to hit the network
                    val product = apiService.getProductById(id)

                    // If successful, it exists
                    id to AvailabilityStatus.Available(DataMapper.mapProductResponseToDomain(product))

                } catch (e: HttpException) {
                    // Server responded (Online)
                    if (e.code() == 404 || e.code() == 400) {
                        // Server says "Deleted"
                        id to AvailabilityStatus.Unavailable
                    } else {
                        // Server says "500 Error", etc. Assume available.
                        id to AvailabilityStatus.Unknown
                    }
                } catch (_: IOException) {
                    // *** THIS HANDLES OFFLINE ***
                    // IOException means No Internet, Timeout, or Connection Reset.
                    // We can't verify
                    id to AvailabilityStatus.Unknown
                } catch (_: Exception) {
                    // Unknown error, stay safe
                    id to AvailabilityStatus.Unknown
                }
            }
        }

        checks.awaitAll().toMap()
    }
}