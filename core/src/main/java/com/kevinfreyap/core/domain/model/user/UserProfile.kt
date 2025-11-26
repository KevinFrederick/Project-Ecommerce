package com.kevinfreyap.core.domain.model.user

import com.google.firebase.firestore.PropertyName
import com.kevinfreyap.core.utils.Constants.FIELD_ADDRESS
import com.kevinfreyap.core.utils.Constants.FIELD_EMAIL
import com.kevinfreyap.core.utils.Constants.FIELD_ID
import com.kevinfreyap.core.utils.Constants.FIELD_IS_GOOGLE_ACCOUNT
import com.kevinfreyap.core.utils.Constants.FIELD_NAME
import com.kevinfreyap.core.utils.Constants.FIELD_PHOTO_URL

data class UserProfile(
    @get:PropertyName(FIELD_ID)
    val uid: String = "",

    @get:PropertyName(FIELD_EMAIL)
    val email: String? = null,

    @get:PropertyName(FIELD_NAME)
    val displayName: String? = null,

    @get:PropertyName(FIELD_PHOTO_URL)
    val photoUrl: String? = null,

    @get:PropertyName(FIELD_ADDRESS)
    val address: UserAddress? = null,

    @get:PropertyName(FIELD_IS_GOOGLE_ACCOUNT)
    val isGoogleAccount: Boolean = false
)