package com.kevinfreyap.auth.ui.login

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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.kevinfreyap.auth.databinding.FragmentLoginBinding
import com.kevinfreyap.shared_ui.R as sharedR
import com.kevinfreyap.auth.R
import com.kevinfreyap.auth.ui.util.getErrorMessage
import com.kevinfreyap.core.data.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeValidationErrors()
        observeLoginState()
        setupRegisterLink()

        binding.btnLogin.setOnClickListener {
            val email = binding.emailInputLogin.getText()
            val pass = binding.passwordInputLogin.getText()

            viewModel.login(
                email = email,
                pass = pass
            )
        }

        binding.emailInputLogin.editText.addTextChangedListener {
            viewModel.clearEmailError()
        }

        binding.passwordInputLogin.editText.addTextChangedListener {
            viewModel.clearPassError()
        }
    }

    private fun observeValidationErrors() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.emailError.collect { errorType ->
                        val errorMessage = errorType?.let { getErrorMessage(it) }
                        binding.emailInputLogin.setError(errorMessage)
                    }
                }

                launch {
                    viewModel.passError.collect { errorType ->
                        val errorMessage = errorType?.let { getErrorMessage(it) }
                        binding.passwordInputLogin.setError(errorMessage)
                    }
                }
            }
        }
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loginState.collect { resource ->
                        when(resource) {
                            is Resource.Loading -> {
                                binding.progressBar.isVisible = true
                            }
                            is Resource.Success -> {
                                binding.progressBar.isVisible = false
                                Toast.makeText(context, getString(sharedR.string.success_login), Toast.LENGTH_SHORT).show()

                                val uri = "app://ecommerce/account".toUri()
                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.loginFragment, true)
                                    .build()

                                findNavController().navigate(
                                    uri,
                                    navOptions
                                )
                            }
                            is Resource.Error -> {
                                binding.progressBar.isVisible = false

                                val message = when(resource.message) {
                                    "ERROR_USER_NOT_FOUND" -> {
                                        getString(sharedR.string.error_user_not_found)
                                    }
                                    "ERROR_WRONG_PASSWORD" -> {
                                        getString(sharedR.string.error_wrong_password)
                                    }
                                    else -> {
                                        getString(sharedR.string.error_unknown)
                                    }
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

    private fun setupRegisterLink() {
        val registerText = getString(sharedR.string.sign_up)
        val fullText = getString(sharedR.string.no_account_sign_up, registerText)
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isFakeBoldText = true
                ds.isUnderlineText = true
            }
        }

        val startIndex = fullText.indexOf(registerText)
        val endIndex = startIndex + registerText.length

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.btnToRegister.highlightColor = Color.TRANSPARENT
        binding.btnToRegister.text = spannableString
        binding.btnToRegister.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}