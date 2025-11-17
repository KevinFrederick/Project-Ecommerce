package com.kevinfreyap.transaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kevinfreyap.core.domain.model.order.OrderItem
import com.kevinfreyap.shared_ui.databinding.ItemCartBinding
import com.kevinfreyap.transaction.viewholder.OrderItemViewHolder

class OrderItemAdapter(

): ListAdapter<OrderItem, OrderItemViewHolder>(ORDER_DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderItemViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OrderItemViewHolder,
        position: Int
    ) {
        val orderItem = getItem(position)
        if (orderItem != null) {
            holder.bind(orderItem)
        }
    }


    companion object {
        val ORDER_DIFF_CALLBACK = object : DiffUtil.ItemCallback<OrderItem>() {
            override fun areItemsTheSame(
                oldItem: OrderItem,
                newItem: OrderItem
            ): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(
                oldItem: OrderItem,
                newItem: OrderItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}