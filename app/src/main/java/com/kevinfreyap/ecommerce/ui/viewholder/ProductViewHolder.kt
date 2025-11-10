package com.kevinfreyap.ecommerce.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.ecommerce.databinding.ItemProductBinding
import com.kevinfreyap.core.domain.model.product.Product

class ProductViewHolder(
    val binding: ItemProductBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product, onItemClick: (productId: Int) -> Unit) {
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
        Glide.with(binding.root)
            .load(product.images[0])
            .placeholder(shimmerDrawable)
            .error(R.drawable.ic_image_24)
            .into(binding.ivProduct)
        binding.tvProduct.text = product.title
        binding.tvProductPrice.text = context.getString(R.string.currency_dollar, product.price)
        binding.tvCategory.text = product.category.name

        binding.root.setOnClickListener {
            onItemClick(product.id)
        }
    }
}