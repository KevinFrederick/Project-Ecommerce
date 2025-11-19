package com.kevinfreyap.wishlist.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.wishlist.databinding.ItemWishlistBinding

class WishlistViewHolder(
    private val binding: ItemWishlistBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(
        product: Product,
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
            .load(product.images.firstOrNull())
            .placeholder(shimmerDrawable)
            .error(R.drawable.ic_image_24)
            .into(binding.ivProduct)

        binding.tvCategory.text = product.category.name
        binding.tvProduct.text = product.title
        binding.tvProductPrice.text = context.getString(R.string.currency_dollar, product.price)

        binding.ivWishlistIcon.setImageResource(R.drawable.ic_favorite_24)

        binding.ivWishlistIcon.setOnClickListener {
            onWishlistClick(product.id)
        }

        itemView.setOnClickListener {
            onItemClick(product.id)
        }
    }
}