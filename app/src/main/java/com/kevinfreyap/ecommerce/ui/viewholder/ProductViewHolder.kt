package com.kevinfreyap.ecommerce.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.ecommerce.databinding.ItemProductBinding
import com.kevinfreyap.core.domain.model.product.Product

class ProductViewHolder(
    val binding: ItemProductBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product) {
        val context =itemView.context
        Glide.with(binding.root)
            .load(product.images[0])
            .into(binding.ivProduct)
        binding.tvProduct.text = product.title
        binding.tvProductPrice.text = context.getString(R.string.currency_dollar, product.price)
    }
}