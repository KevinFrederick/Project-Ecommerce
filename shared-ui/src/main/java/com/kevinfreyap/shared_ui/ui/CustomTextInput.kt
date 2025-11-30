package com.kevinfreyap.shared_ui.ui

import android.content.Context
import android.content.res.ColorStateList
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kevinfreyap.core.utils.dpToPx
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.shared_ui.databinding.ViewCustomTextInputBinding

class CustomTextInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {
    private val binding: ViewCustomTextInputBinding

    val editText: TextInputEditText
        get() = binding.etCustomTextInputEditText

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ViewCustomTextInputBinding.inflate(inflater, this, true)

        // attrs : set of raw XML attributes that are passed from your layout XML to your custom view's constructor
        // like app:hint="Email" and android:inputType="textPassword"

        // R.styleable.CustomTextInput : attributes you want to retrieve
        // From all the attributes provided, I only care about the ones defined in CustomTextInput

        // 0 (defStyleAttr) : optional theme attribute that can provide default styling | 0 means don't look for a default style in the theme.
        // 0 (defStyleRes) : optional style resource that can provide default styling | 0 means don't use a default style resource.
        context.withStyledAttributes(attrs, R.styleable.CustomTextInput, 0, 0) {
            val hint = getString(R.styleable.CustomTextInput_custom_hint)
            val placeholder = getString(R.styleable.CustomTextInput_custom_placeholder)
            if (!placeholder.isNullOrEmpty()) {
                setPlaceholder(placeholder)
            } else {
                setHint(hint)
            }

            val enabled = getBoolean(R.styleable.CustomTextInput_android_enabled, true)
            binding.etCustomTextInputEditText.isEnabled = enabled
            if (!enabled){
                binding.tilCustomTextInputLayout.alpha = 0.6f
            }

            val imeOptions = getInt(R.styleable.CustomTextInput_android_imeOptions, EditorInfo.IME_NULL)
            if (imeOptions != EditorInfo.IME_NULL) {
                binding.etCustomTextInputEditText.imeOptions = imeOptions
            }

            val startIcon = getResourceId(R.styleable.CustomTextInput_custom_start_icon, 0)
            if (startIcon != 0) {
                val color = ContextCompat.getColor(context, R.color.grey_400)
                binding.tilCustomTextInputLayout.setStartIconDrawable(startIcon)
                binding.tilCustomTextInputLayout.setStartIconTintList(ColorStateList.valueOf(color))
            }

            val inputType = getInt(R.styleable.CustomTextInput_android_inputType, InputType.TYPE_CLASS_TEXT)
            setInputType(inputType)

            val endIconMode =getInt(R.styleable.CustomTextInput_custom_endIconMode, TextInputLayout.END_ICON_CLEAR_TEXT)
            setEndIconMode(endIconMode)
        }
    }

    fun getText(): String {
        return binding.etCustomTextInputEditText.text.toString()
    }

    fun setHint(hint: String?) {
        binding.tilCustomTextInputLayout.isHintEnabled = true
        binding.tilCustomTextInputLayout.hint = hint
        binding.etCustomTextInputEditText.hint = null
    }

    fun setPlaceholder(text: String?) {
        binding.tilCustomTextInputLayout.isHintEnabled = false

        val padding = 8.dpToPx
        binding.etCustomTextInputEditText.setPadding(
            binding.etCustomTextInputEditText.paddingStart,
            padding,
            binding.etCustomTextInputEditText.paddingEnd,
            padding
        )
        binding.etCustomTextInputEditText.hint = text
    }

    fun setText(text: String) {
        binding.etCustomTextInputEditText.setText(text)
    }

    fun setInputType(type: Int) {
        binding.etCustomTextInputEditText.inputType = type
    }

    fun setEndIconMode(mode: Int) {
        binding.tilCustomTextInputLayout.endIconMode = mode
    }

    fun setError(error: String?) {
        binding.tilCustomTextInputLayout.error = error
    }
}