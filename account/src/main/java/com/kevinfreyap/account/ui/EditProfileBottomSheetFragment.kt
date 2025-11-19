package com.kevinfreyap.account.ui

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
import com.kevinfreyap.account.databinding.BottomSheetFragmentEditProfileBinding
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_ui.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: AccountViewModel by viewModels()

    private var _binding: BottomSheetFragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = BottomSheetFragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = arguments?.getString(ACCOUNT_EMAIL)
        binding.etEmail.setText(email ?: getString(R.string.text_default_email))

        val name = arguments?.getString(ACCOUNT_NAME)
        binding.etChangeName.setText(name ?: "")

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etChangeName.getText()
            viewModel.updateName(name)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.updateNameState.collect { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading

                        if (resource is Resource.Success) {
                            val resultBundle = bundleOf(
                                IS_UPDATE_SUCCESS to true
                            )
                            setFragmentResult(EDIT_PROFILE_REQ, resultBundle)
                            dismiss()
                            viewModel.resetUpdateState()
                        }

                        if (resource is Resource.Error) {
                            val message = when(resource.message) {
                                "ERROR_USER_NOT_FOUND" -> {
                                    getString(R.string.error_user_not_found)
                                }
                                else -> {
                                    getString(R.string.error_unknown)
                                }
                            }
                            Snackbar.make(
                                binding.root,
                                message,
                                Snackbar.LENGTH_SHORT
                            ).show()
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
        const val EDIT_PROFILE_BOTTOM_SHEET = "EditProfileBottomSheet"
        const val ACCOUNT_EMAIL = "account_email"
        const val ACCOUNT_NAME = "account_name"

        const val EDIT_PROFILE_REQ = "edit_profile_request"
        const val IS_UPDATE_SUCCESS = "is_update_success"
    }
}