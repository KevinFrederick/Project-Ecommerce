package com.kevinfreyap.transaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.transaction.databinding.ItemTransactionHistoryBinding
import com.kevinfreyap.transaction.viewholder.TransactionViewHolder

class TransactionAdapter(
    private val onItemClick: (OrderReceipt) -> Unit
) : ListAdapter<OrderReceipt, TransactionViewHolder>(TRANSACTION_DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        val binding = ItemTransactionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int
    ) {
        val receipt = getItem(position)
        if (receipt != null) {
            holder.bind(
                receipt,
                onItemClick
            )
        }
    }

    companion object {
        val TRANSACTION_DIFF_CALLBACK = object : DiffUtil.ItemCallback<OrderReceipt>() {
            override fun areItemsTheSame(
                oldItem: OrderReceipt,
                newItem: OrderReceipt
            ): Boolean {
                return oldItem.orderId == newItem.orderId
            }

            override fun areContentsTheSame(
                oldItem: OrderReceipt,
                newItem: OrderReceipt
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}