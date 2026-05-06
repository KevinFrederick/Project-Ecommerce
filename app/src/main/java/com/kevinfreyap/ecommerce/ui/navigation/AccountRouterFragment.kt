package com.kevinfreyap.ecommerce.ui.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.kevinfreyap.ecommerce.R
import com.kevinfreyap.shared_auth.domain.usecase.LoginStatusUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountRouterFragment: Fragment() {
    @Inject
    lateinit var loginStatus: LoginStatusUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_account_router, true) // Remove this router from back stack
            .build()

        if (loginStatus()){
            findNavController().navigate(
                R.id.action_navigation_account_router_to_accountProfile,
                null,
                navOptions
            )
        } else {
            findNavController().navigate(
                R.id.action_navigation_account_router_to_loginFragment,
                null,
                navOptions
            )
        }
    }
}