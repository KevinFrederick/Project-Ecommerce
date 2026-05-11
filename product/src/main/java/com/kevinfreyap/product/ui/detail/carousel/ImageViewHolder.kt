package com.kevinfreyap.product.ui.detail.carousel

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.kevinfreyap.product.databinding.ItemCarouselImageBinding
import com.kevinfreyap.shared_ui.R

class ImageViewHolder(
    private val binding: ItemCarouselImageBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(imageUrl: String) {
        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(0.7f)
            .setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }

        Glide.with(itemView.context)
            .load(imageUrl)
            .placeholder(shimmerDrawable)
            .error(R.drawable.ic_image_24)
            .into(binding.ivProductCarouselImage)
    }
}