package com.kevinfreyap.voucher.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kevinfreyap.core.domain.model.voucher.Voucher
import com.kevinfreyap.voucher.databinding.ItemVoucherBinding
import com.kevinfreyap.voucher.viewholder.VoucherViewHolder

class VoucherAdapter : ListAdapter<Voucher, VoucherViewHolder>(VOUCHER_DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VoucherViewHolder {
        val binding = ItemVoucherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VoucherViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: VoucherViewHolder,
        position: Int
    ) {
        val voucher = getItem(position)
        if (voucher != null) {
            holder.bind(voucher)
        }
    }

    companion object {
        private val VOUCHER_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Voucher>() {
            override fun areItemsTheSame(
                oldItem: Voucher,
                newItem: Voucher
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Voucher,
                newItem: Voucher
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}