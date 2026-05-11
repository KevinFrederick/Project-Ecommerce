package com.kevinfreyap.wishlist.viewholder

import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.shared_wishlist.domain.model.WishlistItem
import com.kevinfreyap.wishlist.databinding.ItemWishlistBinding

class WishlistViewHolder(
    private val binding: ItemWishlistBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(
        wishlist: WishlistItem,
        onItemClick: (productId: String) -> Unit,
        onWishlistClick: (productId: String) -> Unit
    ) {
        val context = itemView.context

        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(0.7f)
            .setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }

        Glide.with(context)
            .load(wishlist.productImage)
            .placeholder(shimmerDrawable)
            .error(R.drawable.ic_image_24)
            .into(binding.ivProduct)

        binding.tvCategory.text = wishlist.productCategory
        binding.tvProduct.text = wishlist.productName
        binding.tvProductPrice.text = context.getString(R.string.currency_dollar, wishlist.productPrice)

        binding.ivWishlistIcon.setImageResource(R.drawable.ic_favorite_24)

        if (wishlist.isAvailable) {
            binding.productRoot.alpha = 1.0f
            binding.tvProductUnavailable.isVisible = false

            itemView.setOnClickListener {
                onItemClick(wishlist.productId)
            }
        } else {
            binding.productRoot.alpha = 0.3f
            binding.tvProductUnavailable.isVisible = true
            itemView.setOnClickListener {
                Toast.makeText(context, context.getString(R.string.error_unavailable_product), Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivWishlistIcon.setOnClickListener {
            onWishlistClick(wishlist.productId)
        }
    }
}