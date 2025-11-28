package com.kevinfreyap.transaction.viewholder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.model.order.OrderStatus
import com.kevinfreyap.core.utils.TimeUtils
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.transaction.databinding.ItemTransactionHistoryBinding

class TransactionViewHolder(
    private val binding: ItemTransactionHistoryBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(receipt: OrderReceipt, onItemClick: (OrderReceipt) -> Unit) {
        val context = itemView.context

        binding.tvOrderId.text = receipt.orderId
        binding.tvTotalItems.text = if (receipt.itemsPurchased.size > 1) {
            context.getString(R.string.items, receipt.itemsPurchased.size.toString())
        } else {
            context.getString(R.string.item, receipt.itemsPurchased.size.toString())
        }
        binding.tvOrderPrice.text = context.getString(R.string.currency_dollar, receipt.totalPaid)
        binding.tvOrderDate.text = TimeUtils.formatTransactionTime(receipt.datePlaced)
        itemView.setOnClickListener {
            onItemClick(receipt)
        }

        binding.tvOrderStatus.text = receipt.orderStatus.displayName

        val color = when(receipt.orderStatus) {
            OrderStatus.PROCESSING -> ContextCompat.getColor(context, R.color.blue_300)
            OrderStatus.SHIPPED -> ContextCompat.getColor(context, R.color.blue_700)
            OrderStatus.DELIVERED -> ContextCompat.getColor(context, R.color.green_500)
            OrderStatus.CANCELLED -> ContextCompat.getColor(context, R.color.red_500)
        }
        binding.tvOrderStatus.setTextColor(color)
    }
}