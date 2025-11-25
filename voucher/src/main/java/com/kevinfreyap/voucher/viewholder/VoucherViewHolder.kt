package com.kevinfreyap.voucher.viewholder

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kevinfreyap.core.domain.model.voucher.Voucher
import com.kevinfreyap.core.utils.DateHelper
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.voucher.databinding.ItemVoucherBinding

class VoucherViewHolder(
    private val binding: ItemVoucherBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(voucher: Voucher){
        val context = itemView.context

        binding.tvVoucherTitle.text = voucher.title
        binding.tvVoucherDesc.text = voucher.description
        binding.tvVoucherCode.text = voucher.code

        val dateString = DateHelper.formatMillisToFullDate(voucher.expiryDate)
        binding.tvExpiry.text = context.getString(R.string.exp_date, dateString)

        if (voucher.isPercentage) {
            binding.ivVoucherIcon.setImageResource(R.drawable.ic_percent_24)
        } else {
            binding.ivVoucherIcon.setImageResource(R.drawable.ic_dollar_24)
        }

        binding.tvVoucherCode.isVisible = !voucher.isUsed
        binding.tvVoucherUsed.isVisible = voucher.isUsed

        binding.tvNewVoucher.isVisible = voucher.isNew

        if (voucher.isActive()) {
            binding.percentageContainer.alpha = 1.0f
            binding.voucherDescription.alpha = 1.0f
            binding.tvVoucherCode.setOnClickListener {
                val textToCopy = binding.tvVoucherCode.text.toString()
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Voucher code", textToCopy)

                clipboard.setPrimaryClip(clip)

                Toast.makeText(context, context.getString(R.string.success_copy_clipboard), Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.percentageContainer.alpha = 0.3f
            binding.voucherDescription.alpha = 0.3f
            binding.tvNewVoucher.isVisible = false
        }
    }
}