package com.kevinfreyap.shared_ui.util

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.kevinfreyap.shared_ui.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun Fragment.setupCartMenu(
    cartItemCount: Flow<Int>,
    onCartClicked: () -> Unit
) {
    val badge = BadgeDrawable.create(requireContext()).apply {
        isVisible = false
        badgeGravity = BadgeDrawable.TOP_END
    }

    requireActivity().addMenuProvider(object : MenuProvider {
        @OptIn(ExperimentalBadgeUtils::class)
        override fun onCreateMenu(
            menu: Menu,
            menuInflater: MenuInflater
        ) {
            menuInflater.inflate(R.menu.cart_menu, menu)

            val cartItem = menu.findItem(R.id.menu_cart)
            val cartView = cartItem?.actionView as? FrameLayout
            val cartIconImage = cartView?.findViewById<ImageView>(R.id.btn_cart_image)

            if (cartIconImage != null) {
                // Post the attachment to the actionView's message queue.
                // This delays execution until after the layout and animation pass.
                cartView.post {
                    BadgeUtils.attachBadgeDrawable(badge, cartIconImage, cartView)
                }

                cartIconImage.setOnClickListener {
                    onCartClicked()
                }
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return false
        }

    }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            cartItemCount.collect { count ->
                if (count > 0 ) {
                    badge.number = count
                    badge.isVisible = true
                } else {
                    badge.isVisible = false
                }
            }
        }
    }
}