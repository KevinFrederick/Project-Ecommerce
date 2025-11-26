package com.kevinfreyap.account.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kevinfreyap.account.databinding.FragmentAccountBinding
import com.kevinfreyap.shared_ui.R as sharedR
import com.kevinfreyap.account.R
import com.kevinfreyap.account.ui.EditAddressBottomSheetFragment.Companion.ACCOUNT_ADDRESS
import com.kevinfreyap.account.ui.EditAddressBottomSheetFragment.Companion.EDIT_ADDRESS_BOTTOM_SHEET
import com.kevinfreyap.account.ui.EditAddressBottomSheetFragment.Companion.EDIT_ADDRESS_REQ
import com.kevinfreyap.account.ui.EditAddressBottomSheetFragment.Companion.IS_ADDRESS_UPDATE_SUCCESS
import com.kevinfreyap.account.ui.EditProfileBottomSheetFragment.Companion.ACCOUNT_EMAIL
import com.kevinfreyap.account.ui.EditProfileBottomSheetFragment.Companion.ACCOUNT_NAME
import com.kevinfreyap.account.ui.EditProfileBottomSheetFragment.Companion.EDIT_PROFILE_BOTTOM_SHEET
import com.kevinfreyap.account.ui.EditProfileBottomSheetFragment.Companion.EDIT_PROFILE_REQ
import com.kevinfreyap.account.ui.EditProfileBottomSheetFragment.Companion.IS_PROFILE_UPDATE_SUCCESS
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.model.user.UserProfile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private val viewModel: AccountViewModel by viewModels()

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private var userEmail: String? = null
    private var userName: String? = null
    private var userAddress: UserAddress? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserProfile()

        binding.btnWishlist.setOnClickListener {
            val uri = "app://ecommerce/wishlist".toUri()
            findNavController().navigate(uri)
        }

        binding.btnVoucher.setOnClickListener {
            val uri = "app://ecommerce/voucher".toUri()
            findNavController().navigate(uri)
        }

        binding.btnSettings.setOnClickListener {
            val uri = "app://ecommerce/settings".toUri()
            findNavController().navigate(uri)
        }

        binding.btnEditAccount.setOnClickListener {
            val editAccountBottomSheetFragment = EditProfileBottomSheetFragment()
            val profileBundle = bundleOf(
                ACCOUNT_EMAIL to userEmail,
                ACCOUNT_NAME to userName
            )

            editAccountBottomSheetFragment.arguments = profileBundle
            editAccountBottomSheetFragment.show(childFragmentManager, EDIT_PROFILE_BOTTOM_SHEET)
        }

        binding.btnEditAddress.setOnClickListener {
            val editAddressBottomSheetFragment = EditAddressBottomSheetFragment()
            val addressBundle = bundleOf(
                ACCOUNT_ADDRESS to userAddress
            )

            editAddressBottomSheetFragment.arguments = addressBundle
            editAddressBottomSheetFragment.show(childFragmentManager, EDIT_ADDRESS_BOTTOM_SHEET)
        }

        childFragmentManager.setFragmentResultListener(EDIT_PROFILE_REQ, viewLifecycleOwner) { requestKey, result ->
            val isSuccess = result.getBoolean(IS_PROFILE_UPDATE_SUCCESS)
            if (isSuccess) {
                showSnackBar(getString(sharedR.string.success_update_name))
            }
        }

        childFragmentManager.setFragmentResultListener(EDIT_ADDRESS_REQ, viewLifecycleOwner) { requestKey, result ->
            val isSuccess = result.getBoolean(IS_ADDRESS_UPDATE_SUCCESS)
            if (isSuccess) {
                showSnackBar(getString(sharedR.string.success_update_address))
            }
        }
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userProfile.collect { userProfile ->
                        when(userProfile) {
                            is Resource.Loading -> {
                                binding.progressBar.isVisible = true
                            }
                            is Resource.Success -> {
                                binding.progressBar.isVisible = false

                                val currentUser = userProfile.data
                                userAddress = currentUser.address
                                if (currentUser.uid.isNotEmpty()) {
                                    setProfile(currentUser)
                                    userEmail = currentUser.email
                                    userName = currentUser.displayName
                                } else {
                                    setNavigateAuth()
                                }
                            }
                            is Resource.Error -> {
                                binding.progressBar.isVisible = false

                                val message = when(userProfile.message) {
                                    "ERROR_USER_NOT_FOUND" -> {
                                        getString(sharedR.string.error_user_not_found)
                                    }
                                    "ERROR_NO_CONNECTION" -> {
                                        getString(sharedR.string.error_no_connection)
                                    }
                                    else -> {
                                        getString(sharedR.string.error_unknown)
                                    }
                                }
                                showSnackBar(message)
                            }
                        }

                    }
                }

                launch {
                    viewModel.newVoucherCount.collect { value ->
                        binding.newVoucherBadge.isVisible = value > 0
                    }
                }
            }
        }
    }

    private fun setProfile(user: UserProfile) {
        binding.tvAccountName.text = if (user.displayName.isNullOrEmpty()) {
            getString(sharedR.string.text_unknown_name)
        } else {
            user.displayName
        }
        binding.tvAccountEmail.text = user.email

        binding.tvAccountAddress.text = if (user.address == null) {
            getString(sharedR.string.text_no_address)
        } else {
            user.address.toString()
        }

        Glide.with(requireContext())
            .load(user.photoUrl)
            .placeholder(R.drawable.ic_account_circle_24)
            .error(R.drawable.ic_account_circle_24)
            .into(binding.ivAccountProfile)
    }

    private fun setNavigateAuth() {
        val startDestinationId = findNavController().graph.startDestinationId
        val navOptions = NavOptions.Builder()
            .setPopUpTo(startDestinationId, false)
            .build()
        val uri = "app://ecommerce/account".toUri()
        findNavController().navigate(
            uri,
            navOptions
        )
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}