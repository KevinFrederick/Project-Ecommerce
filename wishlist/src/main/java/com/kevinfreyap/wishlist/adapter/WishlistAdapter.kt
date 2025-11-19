package com.kevinfreyap.wishlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.wishlist.databinding.ItemWishlistBinding
import com.kevinfreyap.wishlist.viewholder.WishlistViewHolder

class WishlistAdapter(
    private val onItemClick: (productId: String) -> Unit,
    private val onWishlistClick: (productId: String) -> Unit
): ListAdapter<Product, WishlistViewHolder>(WISHLIST_DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WishlistViewHolder {
        val binding = ItemWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WishlistViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: WishlistViewHolder,
        position: Int
    ) {
        val product = getItem(position)
        if (product != null) {
            holder.bind(product, onItemClick, onWishlistClick)
        }
    }

    companion object {
        val WISHLIST_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}