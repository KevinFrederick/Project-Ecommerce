package com.kevinfreyap.wishlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kevinfreyap.shared_wishlist.domain.model.WishlistItem
import com.kevinfreyap.wishlist.databinding.ItemWishlistBinding
import com.kevinfreyap.wishlist.viewholder.WishlistViewHolder

class WishlistAdapter(
    private val onItemClick: (productId: String) -> Unit,
    private val onWishlistClick: (productId: String) -> Unit
): ListAdapter<WishlistItem, WishlistViewHolder>(WISHLIST_DIFF_CALLBACK) {
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
        val wishlistItem = getItem(position)
        if (wishlistItem != null) {
            holder.bind(wishlistItem, onItemClick, onWishlistClick)
        }
    }

    companion object {
        val WISHLIST_DIFF_CALLBACK = object : DiffUtil.ItemCallback<WishlistItem>() {
            override fun areItemsTheSame(
                oldItem: WishlistItem,
                newItem: WishlistItem
            ): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(
                oldItem: WishlistItem,
                newItem: WishlistItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}