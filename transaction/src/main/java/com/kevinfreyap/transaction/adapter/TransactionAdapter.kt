package com.kevinfreyap.transaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt
import com.kevinfreyap.transaction.databinding.ItemTransactionHistoryBinding
import com.kevinfreyap.transaction.viewholder.TransactionViewHolder

class TransactionAdapter(
    private val onItemClick: (TransactionReceipt) -> Unit
) : ListAdapter<TransactionReceipt, TransactionViewHolder>(TRANSACTION_DIFF_CALLBACK) {
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
        val TRANSACTION_DIFF_CALLBACK = object : DiffUtil.ItemCallback<TransactionReceipt>() {
            override fun areItemsTheSame(
                oldItem: TransactionReceipt,
                newItem: TransactionReceipt
            ): Boolean {
                return oldItem.orderId == newItem.orderId
            }

            override fun areContentsTheSame(
                oldItem: TransactionReceipt,
                newItem: TransactionReceipt
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}