package com.kevinfreyap.auth.ui.reset_password

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.auth.databinding.DialogResetPasswordBinding
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult

class ResetPasswordDialog: DialogFragment() {
    private var _binding: DialogResetPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DialogResetPasswordBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSend.setOnClickListener {
            val email = binding.etEmail.getText()
            if (email.isNotBlank()){
                val emailBundle = bundleOf(
                    EMAIL_INPUT to email
                )
                setFragmentResult(RESET_PASSWORD_REQ, emailBundle)
                dismiss()
            } else {
                binding.etEmail.setError(getString(R.string.error_email_is_required))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val RESET_PASSWORD_DIALOG = "reset_password_dialog"
        const val RESET_PASSWORD_REQ = "reset_password_req"
        const val EMAIL_INPUT = "email_input"
    }
}