package com.kevinfreyap.product.ui.detail.carousel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kevinfreyap.product.databinding.ItemCarouselImageBinding

class ImageCarouselAdapter: ListAdapter<String, ImageViewHolder>(IMAGE_DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewHolder {
        val binding = ItemCarouselImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ImageViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val IMAGE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}