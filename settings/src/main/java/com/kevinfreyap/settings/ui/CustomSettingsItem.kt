package com.kevinfreyap.settings.ui

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.kevinfreyap.core.utils.dpToPx
import com.kevinfreyap.settings.R
import com.kevinfreyap.settings.databinding.ViewCustomSettingsItemBinding

class CustomSettingsItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {
    val binding: ViewCustomSettingsItemBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = ViewCustomSettingsItemBinding.inflate(inflater, this)

        val padding = 16.dpToPx
        setPadding(padding, padding, padding, padding)

        isClickable = true
        isFocusable = true

        // Default Ripple Background
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)

        context.withStyledAttributes(attrs, R.styleable.CustomSettingsItem, 0, 0) {
            val title = getString(R.styleable.CustomSettingsItem_settingTitle)
            binding.tvSettingsName.text = title

            val iconRes = getResourceId(R.styleable.CustomSettingsItem_settingIcon, 0)
            if (iconRes != 0) {
                setSettingsIcon(iconRes)
            }

            val chevronVisible = getBoolean(R.styleable.CustomSettingsItem_chevronVisible, false)
            binding.ivChevronRight.isVisible = chevronVisible

            val descriptionText = getString(R.styleable.CustomSettingsItem_descriptionText)
            if (descriptionText != null) {
                binding.tvSettingsDesc.isVisible = true
                setDescriptionText(descriptionText)
            } else {
                binding.tvSettingsDesc.isVisible = false
            }
        }
    }

    fun setSettingsIcon(iconRes: Int) {
        binding.ivIcon.setImageResource(iconRes)
    }

    fun setDescriptionText(text: String) {
        binding.tvSettingsDesc.isVisible = true
        binding.tvSettingsDesc.text = text
    }

    fun setOnItemClickListener(listener: () -> Unit) {
        setOnClickListener { listener() }
    }
}