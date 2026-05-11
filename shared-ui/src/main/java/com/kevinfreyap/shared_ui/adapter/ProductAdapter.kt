package com.kevinfreyap.shared_ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kevinfreyap.shared_product.domain.model.Product
import com.kevinfreyap.shared_ui.databinding.ItemProductBinding
import com.kevinfreyap.shared_ui.viewholder.ProductViewHolder

class ProductAdapter(
    private val onItemClicked: (productId: String) -> Unit
): PagingDataAdapter<Product, ProductViewHolder>(PRODUCT_DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        val product = getItem(position)
        if (product != null) {
            holder.bind(product, onItemClicked)
        }
    }

    companion object {
        val PRODUCT_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}