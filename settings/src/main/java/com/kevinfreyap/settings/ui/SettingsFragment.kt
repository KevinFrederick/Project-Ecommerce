package com.kevinfreyap.settings.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kevinfreyap.core.utils.GoogleAuthHelper
import com.kevinfreyap.core.utils.isDarkThemeOn
import com.kevinfreyap.settings.databinding.FragmentSettingsBinding
import com.kevinfreyap.settings.dialog.ThemeDialog
import com.kevinfreyap.settings.dialog.ThemeDialog.Companion.THEME_DIALOG_TAG
import com.kevinfreyap.settings.dialog.ThemeDialog.Companion.THEME_KEY
import com.kevinfreyap.settings.dialog.ThemeDialog.Companion.THEME_REQ
import com.kevinfreyap.shared_ui.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleAuthHelper: GoogleAuthHelper

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

        binding.settingsTheme.setOnClickListener {
            val themeDialog = ThemeDialog()
            val currentMode = bundleOf(
                THEME_KEY to viewModel.currentTheme.value
            )

            themeDialog.arguments = currentMode
            themeDialog.show(childFragmentManager, THEME_DIALOG_TAG)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
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
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}