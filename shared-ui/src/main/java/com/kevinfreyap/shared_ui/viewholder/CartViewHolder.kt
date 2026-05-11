package com.kevinfreyap.shared_ui.viewholder

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.kevinfreyap.shared_cart.domain.model.Cart
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.shared_ui.databinding.ItemCartBinding

class CartViewHolder(
    private val binding: ItemCartBinding,
    private val onNavigation: (Cart) -> Unit,
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

        binding.tvProductName.text = cart.product.title
        binding.tvProductPrice.text = context.getString(R.string.currency_dollar, cart.product.price)
        binding.tvProductQuantity.text = cart.quantity.toString()
        binding.tvProductQuantity.setOnClickListener {}
        binding.tvProductQuantity.isSoundEffectsEnabled = false

        Glide.with(context)
            .load(cart.product.images.firstOrNull())
            .placeholder(shimmerDrawable)
            .error(R.drawable.ic_image_24)
            .into(binding.ivProductImage)

        if (cart.isAvailable) {
            binding.itemCartLayout.alpha = 1.0f
            binding.tvItemUnavailable.isVisible = false

            binding.root.setOnClickListener {
                onNavigation(cart)
            }
        } else {
            binding.itemCartLayout.alpha = 0.3f
            binding.tvItemUnavailable.isVisible = true
            binding.btnIncreaseQty.isEnabled = false
            binding.btnDecreaseQty.isEnabled = false
        }
    }

    fun bindQuantity(cart: Cart) {
        binding.tvProductQuantity.text = cart.quantity.toString()
    }
}