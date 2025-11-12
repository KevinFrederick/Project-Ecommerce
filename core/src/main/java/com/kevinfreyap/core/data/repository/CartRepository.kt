package com.kevinfreyap.core.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.model.cart.SimpleCartItem
import com.kevinfreyap.core.domain.repository.ICartRepository
import com.kevinfreyap.core.domain.repository.IProductRepository
import com.kevinfreyap.core.utils.Constants.CART_SUB_COLLECTION
import com.kevinfreyap.core.utils.Constants.FIELD_QUANTITY
import com.kevinfreyap.core.utils.Constants.USER_COLLECTION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val productRepository: IProductRepository
): ICartRepository {

    override fun getCartItems(): Flow<Resource<List<Cart>>> = flow {
        emit(Resource.Loading())

        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()){
            emit(Resource.Error("ERROR_USER_NOT_FOUND"))
            return@flow
        }

        try {
            val cartList = firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(CART_SUB_COLLECTION)
                .get()
                .await().toObjects<SimpleCartItem>()

            val cartItems = mutableListOf<Cart>()
            val itemsToDeleteFromCart = mutableListOf<String>()

            for (item in cartList) {
                val product = productRepository.getProductByIdFromCache(item.productId).first()

                if (product != null) {
                    cartItems.add(
                        Cart(
                            product = product,
                            quantity = item.quantity
                        )
                    )
                }
                else {
                    Log.w("CartRepository", "Product ${item.productId} not found in cache")
                    itemsToDeleteFromCart.add(
                        item.productId.toString()
                    )
                }
            }

            if (itemsToDeleteFromCart.isNotEmpty()){
                CoroutineScope(Dispatchers.IO).launch {
                    deleteCartItems(currentUserUid, itemsToDeleteFromCart)
                }
            }

            emit(Resource.Success(cartItems))
        } catch (_: IOException) {
            emit(Resource.Error("ERROR_NO_CONNECTION"))

        } catch (_: FirebaseFirestoreException) {
            emit(Resource.Error("ERROR_FAILED_TO_LOAD"))

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override fun addToCart(productId: Int, quantity: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()){
            emit(Resource.Error("ERROR_USER_NOT_FOUND"))
            return@flow
        }

        try {
            val cartItem = SimpleCartItem(productId, quantity)

            firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(CART_SUB_COLLECTION)
                .document(productId.toString())
                .set(cartItem)
                .await()

            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "ERROR_FAILED_TO_ADD_TO_CART"))
        }
    }

    override fun updateItemQuantity(
        productId: Int,
        newQuantity: Int
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()) {
            emit(Resource.Error("ERROR_USER_NOT_FOUND"))
            return@flow
        }

        try {
            val cartItemRef = firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(CART_SUB_COLLECTION)
                .document(productId.toString())

            cartItemRef.update(FIELD_QUANTITY, newQuantity).await()

            emit(Resource.Success(true))
        } catch (_: IOException) {
            emit(Resource.Error("ERROR_NO_CONNECTION"))
        } catch (e: Exception) {
            if (e.message?.contains("network", ignoreCase = true) == true) {
                emit(Resource.Error("ERROR_NO_CONNECTION"))
            }else {
                emit(Resource.Error(e.message ?: "Update Failed"))
            }
        }
    }

    override fun removeItemFromCart(productId: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()) {
            emit(Resource.Error("ERROR_USER_NOT_FOUND"))
            return@flow
        }

        try {
            val cartItemRef = firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(CART_SUB_COLLECTION)
                .document(productId.toString())

            cartItemRef.delete().await()

            emit(Resource.Success(true))
        } catch (_: IOException) {
            emit(Resource.Error("ERROR_NO_CONNECTION"))
        } catch (e: Exception) {
            if (e.message?.contains("network", ignoreCase = true) == true) {
                emit(Resource.Error("ERROR_NO_CONNECTION"))
            }else {
                emit(Resource.Error(e.message ?: "Delete Failed"))
            }
        }
    }

    override fun getCartItemCount(): Flow<Int> {
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()) {
            return flowOf(0) // Return a flow of 0 if logged out
        }

        // 1. Get a reference to the cart collection
        val cartRef = firestore.collection(USER_COLLECTION)
            .document(currentUserUid)
            .collection(CART_SUB_COLLECTION)

        // 2. Use .snapshots() to get a Flow that "listens" for
        //    any changes to the cart (add, remove, etc.)
        return cartRef.snapshots().map { snapshot ->
            // 3. Just return the number of documents (items)
            snapshot.size()
        }.catch {
            // On error (e.g., no network), just emit 0
            emit(0)
        }
    }

    private suspend fun deleteCartItems(uid: String, itemIds: List<String>) {
        val cartRef = firestore.collection(USER_COLLECTION)
            .document(uid)
            .collection(CART_SUB_COLLECTION)

        firestore.runBatch { batch ->
            for (id in itemIds){
                batch.delete(cartRef.document(id))
            }
        }.await()
    }
}