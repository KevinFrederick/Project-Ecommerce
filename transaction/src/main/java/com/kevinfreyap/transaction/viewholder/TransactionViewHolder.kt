package com.kevinfreyap.transaction.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.utils.TimeUtils
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.transaction.databinding.ItemTransactionHistoryBinding

class TransactionViewHolder(
    private val binding: ItemTransactionHistoryBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(receipt: OrderReceipt, onItemClick: (OrderReceipt) -> Unit) {
        val context = itemView.context

        binding.tvOrderStatus.text = receipt.orderStatus
        binding.tvOrderId.text = receipt.orderId
        binding.tvOrderPrice.text = context.getString(R.string.currency_dollar, receipt.totalPaid)
        binding.tvOrderDate.text = TimeUtils.formatTransactionTime(receipt.datePlaced)
        itemView.setOnClickListener {
            onItemClick(receipt)
        }
    }
}