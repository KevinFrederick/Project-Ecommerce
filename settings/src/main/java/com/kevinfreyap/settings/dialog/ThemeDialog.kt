package com.kevinfreyap.settings.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.kevinfreyap.settings.R
import com.kevinfreyap.settings.databinding.DialogThemeBinding
import androidx.core.graphics.drawable.toDrawable
import com.kevinfreyap.settings.ui.SettingsFragment.Companion.THEME_REQ

class ThemeDialog : DialogFragment() {
    private var _binding: DialogThemeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DialogThemeBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.70).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentMode = arguments?.getInt(THEME_KEY) ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

        when(currentMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> binding.rbDarkMode.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> binding.rbLightMode.isChecked = true
            else -> binding.rbSystemMode.isChecked = true
        }

        binding.rgThemeOption.setOnCheckedChangeListener { _, checkedId ->
            val selectedMode = when(checkedId) {
                R.id.rb_dark_mode -> AppCompatDelegate.MODE_NIGHT_YES
                R.id.rb_light_mode -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            setFragmentResult(THEME_REQ, bundleOf(THEME_KEY to selectedMode))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val THEME_DIALOG_TAG = "theme_dialog"
        const val THEME_KEY = "theme_mode_key"
    }
}