package com.kevinfreyap.cart.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.kevinfreyap.cart.databinding.ItemCartBinding
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.shared_ui.R

class CartViewHolder(
    private val binding: ItemCartBinding,
    private val onIncrease: (Cart) -> Unit,
    private val onDecrease: (Cart) -> Unit,
    private val getItem: (Int) -> Cart?
): RecyclerView.ViewHolder(binding.root) {

    init {
        binding.btnIncreaseQty.setOnClickListener {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = getItem(position)
                if (item != null) {
                    onIncrease(item)
                }
            }
        }

        binding.btnDecreaseQty.setOnClickListener {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = getItem(position)
                if (item != null) {
                    onDecrease(item)
                }
            }
        }
    }

    fun bind(cart: Cart) {
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

        binding.tvProductNameBottomSheet.text = cart.product.title
        binding.tvProductPriceBottomSheet.text = context.getString(R.string.currency_dollar, cart.product.price)
        binding.tvProductQuantity.text = cart.quantity.toString()

        Glide.with(context)
            .load(cart.product.images.firstOrNull())
            .placeholder(shimmerDrawable)
            .error(R.drawable.ic_image_24)
            .into(binding.ivProductImageBottomSheet)
    }

    fun bindQuantity(cart: Cart) {
        binding.tvProductQuantity.text = cart.quantity.toString()
    }
}