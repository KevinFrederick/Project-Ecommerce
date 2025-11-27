package com.kevinfreyap.settings.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.kevinfreyap.settings.databinding.DialogReauthBinding
import com.kevinfreyap.shared_ui.R

class ReAuthDialog : DialogFragment() {
    private var _binding: DialogReauthBinding? = null
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
        _binding = DialogReauthBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnDelete.setOnClickListener {
            val password = binding.etPassword.getText()
            if (password.isNotBlank()) {
                setFragmentResult(RE_AUTH_REQ, bundleOf(KEY_PASSWORD to password))
                dismiss()
            } else {
                binding.etPassword.setError(getString(R.string.error_password_is_required))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val RE_AUTH_TAG = "ReAuthDialog"
        const val RE_AUTH_REQ = "re_auth_req"
        const val KEY_PASSWORD = "key_password"
    }
}