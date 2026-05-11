package com.kevinfreyap.transaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kevinfreyap.shared_transaction.domain.model.TransactionItem
import com.kevinfreyap.shared_ui.databinding.ItemCartBinding
import com.kevinfreyap.transaction.viewholder.OrderItemViewHolder

class OrderItemAdapter: ListAdapter<TransactionItem, OrderItemViewHolder>(ORDER_DIFF_CALLBACK) {
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
        val ORDER_DIFF_CALLBACK = object : DiffUtil.ItemCallback<TransactionItem>() {
            override fun areItemsTheSame(
                oldItem: TransactionItem,
                newItem: TransactionItem
            ): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(
                oldItem: TransactionItem,
                newItem: TransactionItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}