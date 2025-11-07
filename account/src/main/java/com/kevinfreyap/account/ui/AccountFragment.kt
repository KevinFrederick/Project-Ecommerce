package com.kevinfreyap.account.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kevinfreyap.account.databinding.FragmentAccountBinding
import com.kevinfreyap.shared_ui.R as sharedR
import com.kevinfreyap.account.R
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.user.UserProfile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private val viewModel: AccountViewModel by viewModels()

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

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

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
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

                                val user = userProfile.data
                                if (user != null) {
                                    setProfile(user)
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
                                    else -> {
                                        getString(sharedR.string.error_unknown)
                                    }
                                }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}