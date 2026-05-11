package com.kevinfreyap.account.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserAddressUi(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val zipCode: String = ""
): Parcelable
