package com.kevinfreyap.shared_ui.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.kevinfreyap.shared_ui.databinding.DialogCustomBinding
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult

class CustomDialog : DialogFragment() {
    private var _binding: DialogCustomBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.80).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DialogCustomBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val requestKey = args.getString(ARG_REQ_KEY)!!

        binding.tvDialogTitle.text = args.getString(ARG_TITLE)
        binding.tvDialogMessage.text = args.getString(ARG_MESSAGE)

        val negativeBtnText = args.getString(ARG_NEG_BTN)
        if (!negativeBtnText.isNullOrBlank()) {
            binding.btnNegative.text = negativeBtnText
            binding.btnNegative.setOnClickListener {
                setFragmentResult(requestKey, bundleOf(RESULT_CONFIRMED to false))
                dismiss()
            }
        }
        binding.btnNegative.isVisible = !negativeBtnText.isNullOrBlank()

        val positiveBtn = args.getString(ARG_POS_BTN)
        if (!positiveBtn.isNullOrBlank()) {
            binding.btnPositive.text = positiveBtn
            binding.btnPositive.setOnClickListener {
                setFragmentResult(requestKey, bundleOf(RESULT_CONFIRMED to true))
                dismiss()
            }
        }
        binding.btnPositive.isVisible = !positiveBtn.isNullOrBlank()

        val dialogIconColor = args.getInt(ARG_ICON_COLOR, 0)
        val dialogIcon = args.getInt(ARG_ICON, 0)
        if (dialogIcon != 0) {
            binding.ivDialogIcon.setImageResource(dialogIcon)
            if (dialogIconColor != 0){
                val colorStateList = ColorStateList.valueOf(dialogIconColor)
                binding.ivDialogIcon.imageTintList = colorStateList
            }
        }
        binding.ivDialogIcon.isVisible = dialogIcon != 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CUSTOM_DIALOG_TAG = "ConfirmationDialog"

        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"
        private const val ARG_POS_BTN = "positive_btn"
        private const val ARG_NEG_BTN = "negative_btn"
        private const val ARG_ICON = "icon"
        private const val ARG_ICON_COLOR = "icon_color"
        private const val ARG_REQ_KEY = "req_key"

        // Result Key
        const val RESULT_CONFIRMED = "is_confirmed"

        fun newInstance(
            requestKey: String,
            title: String,
            message: String,
            positiveText: String? = null,
            negativeText: String? = null,
            icon: Int? = null,
            iconColor: Int? = null
        ): CustomDialog {
            return CustomDialog().apply {
               arguments = bundleOf(
                   ARG_REQ_KEY to requestKey,
                   ARG_TITLE to title,
                   ARG_MESSAGE to message,
                   ARG_POS_BTN to positiveText,
                   ARG_NEG_BTN to negativeText,
               ).apply {
                   if (icon != null) {
                       putInt(ARG_ICON, icon)
                   }
                   if (iconColor != null) {
                       putInt(ARG_ICON_COLOR, iconColor)
                   }
               }
            }
        }
    }
}