package com.kevinfreyap.settings.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kevinfreyap.core.BuildConfig
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.utils.isDarkThemeOn
import com.kevinfreyap.settings.databinding.FragmentSettingsBinding
import com.kevinfreyap.settings.dialog.NotificationDialog
import com.kevinfreyap.settings.dialog.NotificationDialog.Companion.NOTIFICATION_DIALOG_TAG
import com.kevinfreyap.settings.dialog.ReAuthDialog
import com.kevinfreyap.settings.dialog.ReAuthDialog.Companion.KEY_PASSWORD
import com.kevinfreyap.settings.dialog.ReAuthDialog.Companion.RE_AUTH_REQ
import com.kevinfreyap.settings.dialog.ReAuthDialog.Companion.RE_AUTH_TAG
import com.kevinfreyap.settings.dialog.ThemeDialog
import com.kevinfreyap.settings.dialog.ThemeDialog.Companion.THEME_DIALOG_TAG
import com.kevinfreyap.settings.dialog.ThemeDialog.Companion.THEME_KEY
import com.kevinfreyap.settings.ui.ChangePasswordBottomSheetFragment.Companion.CHANGE_PASSWORD_BOTTOM_SHEET
import com.kevinfreyap.settings.ui.ChangePasswordBottomSheetFragment.Companion.IS_SUCCESS
import com.kevinfreyap.shared_auth.data.helper.GoogleAuthHelper
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.shared_ui.ui.CustomDialog
import com.kevinfreyap.shared_ui.ui.CustomDialog.Companion.CUSTOM_DIALOG_TAG
import com.kevinfreyap.shared_ui.ui.CustomDialog.Companion.RESULT_CONFIRMED
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleAuthHelper: GoogleAuthHelper
    private var isGoogleUser: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        googleAuthHelper = GoogleAuthHelper(requireActivity())

        childFragmentManager.setFragmentResultListener(THEME_REQ, viewLifecycleOwner) { _, result ->
            val newMode = result.getInt(THEME_KEY)
            viewModel.setTheme(newMode)
        }

        childFragmentManager.setFragmentResultListener(CHANGE_PASSWORD_REQ, viewLifecycleOwner) { _, result ->
            val isSuccess = result.getBoolean(IS_SUCCESS)
            if (isSuccess) {
                Snackbar.make(binding.root, getString(R.string.success_update_password), Snackbar.LENGTH_SHORT).show()
            }
        }

        childFragmentManager.setFragmentResultListener(SIGN_OUT_REQ, viewLifecycleOwner) { _, result ->
            val resultConfirmed = result.getBoolean(RESULT_CONFIRMED)
            if (resultConfirmed) {
                viewModel.logout()
            }
        }

        childFragmentManager.setFragmentResultListener(DELETE_ACCOUNT_WARNING_REQ, viewLifecycleOwner) { _, result ->
            val resultConfirmed = result.getBoolean(RESULT_CONFIRMED)
            if (resultConfirmed) {
                checkProviderAndReAuth()
            }
        }

        childFragmentManager.setFragmentResultListener(RE_AUTH_REQ, viewLifecycleOwner) { _, result ->
            val password = result.getString(KEY_PASSWORD)
            if (!password.isNullOrBlank()) {
                viewModel.onDeleteAccountWithPassword(password)
            }
        }

        binding.settingsTheme.setOnClickListener {
            val themeDialog = ThemeDialog()
            val currentMode = bundleOf(
                THEME_KEY to viewModel.currentTheme.value
            )

            themeDialog.arguments = currentMode
            themeDialog.show(childFragmentManager, THEME_DIALOG_TAG)
        }

        binding.settingsNotification.setOnClickListener {
            val notificationDialog = NotificationDialog()
            notificationDialog.show(childFragmentManager, NOTIFICATION_DIALOG_TAG)
        }

        binding.settingsChangePassword.setOnClickListener {
            val changePasswordBottomSheet = ChangePasswordBottomSheetFragment()
            changePasswordBottomSheet.show(childFragmentManager, CHANGE_PASSWORD_BOTTOM_SHEET)
        }

        binding.btnLogout.setOnClickListener {
            val btnColor = ContextCompat.getColor(requireContext(), R.color.red_500)
            val iconColor = ContextCompat.getColor(requireContext(), R.color.orange_700)
            val signOutDialog = CustomDialog.newInstance(
                requestKey = SIGN_OUT_REQ,
                title = getString(R.string.sign_out),
                message = getString(R.string.sign_out_description),
                positiveText = getString(R.string.confirm),
                negativeText = getString(R.string.cancel),
                positiveBtnColor = btnColor,
                icon = R.drawable.ic_error_outline_24,
                iconColor = iconColor
            )
            signOutDialog.show(childFragmentManager, CUSTOM_DIALOG_TAG)
        }

        binding.settingsDeleteAccount.setOnClickListener {
            val iconColor = ContextCompat.getColor(requireContext(), R.color.red_500)
            val btnColor = ContextCompat.getColor(requireContext(), R.color.red_500)
            val deleteAccountDialog = CustomDialog.newInstance(
                requestKey = DELETE_ACCOUNT_WARNING_REQ,
                title = getString(R.string.delete_account),
                message = getString(R.string.delete_account_description),
                positiveText = getString(R.string.delete),
                negativeText = getString(R.string.cancel),
                positiveBtnColor = btnColor,
                icon = R.drawable.ic_error_outline_24,
                iconColor = iconColor
            )
            deleteAccountDialog.show(childFragmentManager, CUSTOM_DIALOG_TAG)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.currentUser.collect { resource ->
                        if (resource is Resource.Success) {
                            val isGoogle = resource.data.isGoogleAccount
                            binding.settingsChangePassword.isVisible = !isGoogle

                            isGoogleUser = isGoogle
                        }
                    }
                }

                launch {
                    viewModel.currentTheme.collect { value ->
                        when(value) {
                            AppCompatDelegate.MODE_NIGHT_YES -> {
                                binding.settingsTheme.setDescriptionText(getString(R.string.theme_dark_mode))
                            }
                            AppCompatDelegate.MODE_NIGHT_NO -> {
                                binding.settingsTheme.setDescriptionText(getString(R.string.theme_light_mode))
                            }
                            else -> {
                                binding.settingsTheme.setDescriptionText(getString(R.string.theme_follow_system))
                            }
                        }

                        if (requireContext().isDarkThemeOn()){
                            binding.settingsTheme.setSettingsIcon(R.drawable.ic_nightlight_24)
                        } else {
                            binding.settingsTheme.setSettingsIcon(R.drawable.ic_light_mode_24)
                        }
                    }
                }

                launch {
                    viewModel.navEvent.collect { isNavigate ->
                        if (isNavigate) {
                            googleAuthHelper.signOut()

                            // Wait Google SignOut Finished before navigate
                            findNavController().popBackStack()
                        }
                    }
                }

                launch {
                    viewModel.deleteState.collect { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading

                        if (resource is Resource.Success) {
                            Snackbar.make(binding.root, getString(R.string.success_delete_account), Snackbar.LENGTH_SHORT).show()
                        }

                        if (resource is Resource.Error) {
                            val message = when(resource.message){
                                "ERROR_USER_NOT_FOUND" -> getString(R.string.error_user_not_found)
                                "ERROR_EMAIL_NOT_FOUND" -> getString(R.string.error_email_not_found)
                                else -> getString(R.string.error_unknown)
                            }
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
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

    private fun checkProviderAndReAuth() {
        if (isGoogleUser){
            googleReAuth()
        } else {
            ReAuthDialog().show(childFragmentManager, RE_AUTH_TAG)
        }
    }

    private fun googleReAuth() {
        lifecycleScope.launch {
            val clientId = BuildConfig.WEB_CLIENT_ID
            val idToken = googleAuthHelper.signIn(clientId)

            if (idToken != null) {
                viewModel.onDeleteAccountWithGoogle(idToken)
            } else {
                Snackbar.make(binding.root, getString(R.string.error_verification_failed), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val THEME_REQ = "theme_req"
        const val CHANGE_PASSWORD_REQ = "change_password_req"
        const val DELETE_ACCOUNT_WARNING_REQ = "delete_account_warning_req"
        const val SIGN_OUT_REQ = "sign_out_req"
    }
}