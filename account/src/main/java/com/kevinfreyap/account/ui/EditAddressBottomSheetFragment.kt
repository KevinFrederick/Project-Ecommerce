package com.kevinfreyap.account.ui

import android.os.Build
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
import com.kevinfreyap.account.databinding.BottomSheetFragmentEditAddressBinding
import com.kevinfreyap.account.model.UserAddressUi
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_ui.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditAddressBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: AccountViewModel by viewModels()

    private var _binding: BottomSheetFragmentEditAddressBinding? = null
    private val binding get() = _binding!!

    private var userAddress: UserAddressUi? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = BottomSheetFragmentEditAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAddress = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ACCOUNT_ADDRESS, UserAddressUi::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ACCOUNT_ADDRESS)
        }

        with(binding) {
            etStreetAddress.setText(userAddress?.street ?: "")
            etCityAddress.setText(userAddress?.city ?: "")
            etStateAddress.setText(userAddress?.state ?: "")
            etZipAddress.setText(userAddress?.zipCode ?: "")
            etCountryAddress.setText(userAddress?.country ?: "")
        }

        binding.btnCancelAddress.setOnClickListener {
            dismiss()
        }

        binding.btnSaveAddress.setOnClickListener {
            val street = binding.etStreetAddress.getText()
            val city = binding.etCityAddress.getText()
            val state = binding.etStateAddress.getText()
            val zipCode = binding.etZipAddress.getText()
            val country = binding.etCountryAddress.getText()

            viewModel.updateAddress(
                currentAddress = userAddress,
                newStreet = street,
                newCity = city,
                newState = state,
                newZip = zipCode,
                newCountry = country,
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.updateState.collect { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading

                        if (resource is Resource.Success) {
                            val resultBundle = bundleOf(
                                IS_ADDRESS_UPDATE_SUCCESS to true
                            )
                            setFragmentResult(EDIT_ADDRESS_REQ, resultBundle)
                            dismiss()
                            viewModel.resetUpdateState()
                        }

                        if (resource is Resource.Error) {
                            val message = when(resource.message) {
                                "ERROR_USER_NOT_FOUND" -> {
                                    getString(R.string.error_user_not_found)
                                }
                                "ERROR_STREET_BLANK" -> {
                                    getString(R.string.error_street_blank)
                                }
                                "ERROR_CITY_BLANK" -> {
                                    getString(R.string.error_city_blank)
                                }
                                "ERROR_COUNTRY_BLANK" -> {
                                    getString(R.string.error_country_blank)
                                }
                                "ERROR_INVALID_ZIP" -> {
                                    getString(R.string.error_invalid_zip)
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
        const val EDIT_ADDRESS_BOTTOM_SHEET = "edit_address_bottom_sheet"
        const val ACCOUNT_ADDRESS = "account_address"

        const val EDIT_ADDRESS_REQ = "edit_address_request"
        const val IS_ADDRESS_UPDATE_SUCCESS = "is_address_update_success"
    }
}