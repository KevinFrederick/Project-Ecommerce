package com.kevinfreyap.transaction.viewholder

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.kevinfreyap.core.domain.model.order.OrderItem
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.shared_ui.databinding.ItemCartBinding

class OrderItemViewHolder(
    private val binding: ItemCartBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(orderItem: OrderItem){
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

        binding.tvProductName.text = orderItem.title
        binding.tvProductPrice.text = context.getString(R.string.currency_dollar, orderItem.pricePerItem)
        binding.tvProductQuantity.text = orderItem.quantity.toString()
        Glide.with(context)
            .load(orderItem.imageUrl)
            .placeholder(shimmerDrawable)
            .error(R.drawable.ic_image_24)
            .into(binding.ivProductImage)

        binding.btnIncreaseQty.isEnabled = false
        binding.btnIncreaseQty.isVisible = false

        binding.btnDecreaseQty.isEnabled = false
        binding.btnDecreaseQty.isVisible = false
    }
}