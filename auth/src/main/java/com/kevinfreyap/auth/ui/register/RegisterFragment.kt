package com.kevinfreyap.auth.ui.register

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.auth.databinding.FragmentRegisterBinding
import com.kevinfreyap.auth.ui.util.getErrorMessage
import com.kevinfreyap.core.data.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeValidationErrors()
        observeRegistrationResult()
        setupLoginLink()

        binding.btnRegister.setOnClickListener {
            val email = binding.emailInputRegister.getText()
            val pass = binding.passwordInputRegister.getText()
            val confPass = binding.confirmPasswordInputRegister.getText()
            
            viewModel.register(
                email = email,
                pass = pass,
                confirmPass = confPass
            )
        }

        binding.emailInputRegister.editText.addTextChangedListener {
            viewModel.clearEmailError()
        }

        binding.passwordInputRegister.editText.addTextChangedListener {
            viewModel.clearPasswordError()
        }

        binding.confirmPasswordInputRegister.editText.addTextChangedListener {
            viewModel.clearConfirmPasswordError()
        }
    }

    private fun observeValidationErrors() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.emailError.collect { errorType ->
                        val errorMessage = errorType?.let { getErrorMessage(it) }
                        binding.emailInputRegister.setError(errorMessage)
                    }
                }

                launch {
                    viewModel.passError.collect { errorType ->
                        val errorMessage = errorType?.let { getErrorMessage(it) }
                        binding.passwordInputRegister.setError(errorMessage)
                    }
                }

                launch {
                    viewModel.confirmPassError.collect { errorType ->
                        val errorMessage = errorType?.let { getErrorMessage(it) }
                        binding.confirmPasswordInputRegister.setError(errorMessage)
                    }
                }
            }
        }
    }

    private fun observeRegistrationResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.registerState.collect { resource ->
                        when(resource) {
                            is Resource.Loading -> {
                                binding.progressBar.isVisible = true
                            }
                            is Resource.Success -> {
                                binding.progressBar.isVisible = false
                                Toast.makeText(context, getString(R.string.success_register), Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }
                            is Resource.Error -> {
                                binding.progressBar.isVisible = false
                                val message = if (resource.message == "REGISTRATION FAILED") {
                                    getString(R.string.error_registration)
                                } else {
                                    resource.message
                                }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }

                            null -> {
                                binding.progressBar.isVisible = false
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupLoginLink() {
        val loginText = getString(R.string.sign_in)
        val fullText = getString(R.string.have_account_sign_in, loginText)
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().popBackStack()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isFakeBoldText = true
                ds.isUnderlineText = true
            }
        }

        val startIndex = fullText.indexOf(loginText)
        val endIndex = startIndex + loginText.length

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.btnToLogin.highlightColor = Color.TRANSPARENT
        binding.btnToLogin.text = spannableString
        binding.btnToLogin.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}