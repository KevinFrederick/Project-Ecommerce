package com.kevinfreyap.shared_ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.kevinfreyap.shared_product.domain.model.Product
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.shared_ui.databinding.ItemProductBinding

class ProductViewHolder(
    val binding: ItemProductBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product, onItemClick: (productId: String) -> Unit) {
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
            .load(product.images[0])
            .placeholder(shimmerDrawable)
            .error(R.drawable.ic_image_24)
            .into(binding.ivProduct)
        binding.tvProduct.text = product.title
        binding.tvProductPrice.text = context.getString(R.string.currency_dollar, product.price)
        binding.tvCategory.text = product.category.name

        itemView.setOnClickListener {
            onItemClick(product.id)
        }
    }
}