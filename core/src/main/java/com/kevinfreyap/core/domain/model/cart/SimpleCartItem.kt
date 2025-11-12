package com.kevinfreyap.core.domain.model.cart

import com.google.firebase.firestore.PropertyName
import com.kevinfreyap.core.utils.Constants.FIELD_PRODUCT_ID
import com.kevinfreyap.core.utils.Constants.FIELD_QUANTITY

data class SimpleCartItem(
    @get:PropertyName(FIELD_PRODUCT_ID)
    val productId: Int = 0,

    @get:PropertyName(FIELD_QUANTITY)
    val quantity: Int = 0
)
