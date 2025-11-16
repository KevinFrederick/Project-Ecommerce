package com.kevinfreyap.shared_ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.shared_ui.databinding.ItemCartBinding
import com.kevinfreyap.shared_ui.viewholder.CartViewHolder

class CartAdapter(
    private val onNavigation: (Cart) -> Unit,
    private val onIncrease: (Cart) -> Unit,
    private val onDecrease: (Cart) -> Unit,
): ListAdapter<Cart, CartViewHolder>(CART_DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(
            binding = binding,
            onNavigation = onNavigation,
            onIncrease = onIncrease,
            onDecrease = onDecrease,
        ) { position -> getItem(position)}
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val cart = getItem(position)
        if (cart != null) {
            holder.bind(cart)
        }
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            if (payloads.contains(PAYLOAD_QUANTITY)) {
                holder.bindQuantity(getItem(position))
            } else {
                super.onBindViewHolder(holder, position, payloads)
            }
        }
    }

    companion object {
        private const val PAYLOAD_QUANTITY = "PAYLOAD_QUANTITY"

        val CART_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Cart>() {
            override fun areItemsTheSame(
                oldItem: Cart,
                newItem: Cart
            ): Boolean {
                return oldItem.product.id == newItem.product.id
            }

            override fun areContentsTheSame(
                oldItem: Cart,
                newItem: Cart
            ): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: Cart, newItem: Cart): Any? {
                // If the items are the same, but the content is different...
                if (oldItem.product == newItem.product &&
                    oldItem.isAvailable == newItem.isAvailable &&
                    oldItem.quantity != newItem.quantity) {
                    // ...and ONLY the quantity changed, return our payload
                    return PAYLOAD_QUANTITY
                }
                // Otherwise, return null to force a full re-bind
                return null
            }
        }
    }
}