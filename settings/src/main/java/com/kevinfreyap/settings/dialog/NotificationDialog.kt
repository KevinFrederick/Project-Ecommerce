package com.kevinfreyap.settings.dialog

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.kevinfreyap.settings.databinding.DialogNotificationBinding
import com.kevinfreyap.settings.ui.SettingsViewModel
import com.kevinfreyap.shared_ui.R
import kotlinx.coroutines.launch

class NotificationDialog : DialogFragment() {
    private val viewModel: SettingsViewModel by viewModels({requireParentFragment()})

    private var _binding: DialogNotificationBinding? = null
    private val binding get() = _binding!!

    private var pendingSwitchIsSystem: Boolean = true

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.updateNotificationPreferences(pendingSwitchIsSystem, true)
            updateSwitchVisuals(pendingSwitchIsSystem, true)
        } else {
            updateSwitchVisuals(pendingSwitchIsSystem, false)

            if (pendingSwitchIsSystem) {
                binding.systemNotification.isChecked = false
            } else {
                binding.promotionNotification.isChecked = false
            }

            showPermissionDeniedMessage()
        }
    }

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
        _binding = DialogNotificationBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCloseDialog.setOnClickListener {
            dismiss()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notificationSettings.collect { preferences ->
                binding.systemNotification.setOnCheckedChangeListener(null)
                binding.promotionNotification.setOnCheckedChangeListener(null)

                binding.systemNotification.isChecked = preferences.system
                binding.promotionNotification.isChecked = preferences.promotions

                setupListeners()
            }
        }
    }

    private fun setupListeners(){
        // System Switch
        binding.systemNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkAndRequestPermission(isSystem = true)
            } else {
                viewModel.updateNotificationPreferences(isSystem = true, isEnabled = false)
            }
        }

        // PromotionSwitch
        binding.promotionNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkAndRequestPermission(isSystem = false)
            } else {
                viewModel.updateNotificationPreferences(isSystem = false, isEnabled = false)
            }
        }
    }

    private fun checkAndRequestPermission(isSystem: Boolean) {
        pendingSwitchIsSystem = isSystem

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                viewModel.updateNotificationPreferences(isSystem, true)
            } else {
                requestPermissionLauncher.launch(permission)
            }
        } else {
            viewModel.updateNotificationPreferences(isSystem, true)
        }
    }

    private fun updateSwitchVisuals(isSystem: Boolean, isChecked: Boolean) {
        if (isSystem) {
            binding.systemNotification.isChecked = isChecked
        } else {
            binding.promotionNotification.isChecked = isChecked
        }
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(requireContext(), getString(R.string.error_notification_permission_denied), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val NOTIFICATION_DIALOG_TAG = "notification_dialog"
    }
}