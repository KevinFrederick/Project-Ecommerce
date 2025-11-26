package com.kevinfreyap.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.settings.databinding.BottomSheetFragmentChangePasswordBinding
import com.kevinfreyap.shared_ui.R
import kotlinx.coroutines.launch

class ChangePasswordBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: SettingsViewModel by viewModels({requireParentFragment()})

    private var _binding: BottomSheetFragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = BottomSheetFragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnUpdate.setOnClickListener {
            viewModel.onChangePassword(
                currentPass = binding.etCurrentPass.getText(),
                newPass = binding.etNewPass.getText(),
                confirmPass = binding.etConfPass.getText()
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.updateState.collect { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading

                        if (resource is Resource.Success) {
                            setFragmentResult(CHANGE_PASSWORD_REQ, bundleOf(IS_SUCCESS to true))
                            viewModel.resetUpdateState()
                            dismiss()
                        }

                        if (resource is Resource.Error) {
                            val message = when(resource.message) {
                                "ERROR_CURRENT_PASSWORD_IS_REQUIRED" -> getString(R.string.error_password_is_required)
                                "ERROR_NEW_PASSWORD_IS_REQUIRED" -> getString(R.string.error_new_pass_is_required)
                                "ERROR_CONF_PASSWORD_IS_REQUIRED" -> getString(R.string.error_conf_pass_is_required)
                                "ERROR_PASSWORD_NOT_MATCH" -> getString(R.string.error_new_pass_not_match)
                                "ERROR_PASSWORD_TOO_SHORT" -> getString(R.string.error_password_too_short)
                                "ERROR_PASS_OLD_NEW_SAME" -> getString(R.string.error_new_pass_same)
                                "ERROR_USER_NOT_FOUND" -> getString(R.string.error_user_not_found)
                                "ERROR_EMAIL_NOT_FOUND" -> getString(R.string.error_email_not_found)
                                "ERROR_WRONG_PASSWORD" -> getString(R.string.error_current_password_wrong)
                                else -> getString(R.string.error_unknown)
                            }
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                            viewModel.resetUpdateState()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CHANGE_PASSWORD_BOTTOM_SHEET = "change_password_bottom_sheet"
        const val CHANGE_PASSWORD_REQ = "change_password_req"
        const val IS_SUCCESS = "is_success"
    }
}