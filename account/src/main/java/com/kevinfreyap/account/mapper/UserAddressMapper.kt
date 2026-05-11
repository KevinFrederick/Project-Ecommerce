package com.kevinfreyap.account.mapper

import com.kevinfreyap.account.model.UserAddressUi
import com.kevinfreyap.shared_user.domain.model.UserAddress

fun UserAddress.toUiModel(): UserAddressUi {
    return UserAddressUi(
        street = this.street,
        city = this.city,
        state = this.state,
        country = this.country,
        zipCode = this.zipCode
    )
}