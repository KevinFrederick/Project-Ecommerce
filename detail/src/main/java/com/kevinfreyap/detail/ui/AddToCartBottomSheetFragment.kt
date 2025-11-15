package com.kevinfreyap.detail.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.detail.databinding.BottomSheetFragmentAddToCartBinding
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.shared_ui.util.showGenericDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddToCartBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: DetailViewModel by viewModels({requireParentFragment()})

    private var _binding: BottomSheetFragmentAddToCartBinding? = null
    private val binding get() = _binding!!

    private val shimmer = Shimmer.AlphaHighlightBuilder()
        .setBaseAlpha(0.7f)
        .setHighlightAlpha(0.6f)
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = BottomSheetFragmentAddToCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(PRODUCT_ARG, Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(PRODUCT_ARG)
        }

        if (product != null) {
            binding.tvProductNameBottomSheet.text = product.title
            binding.tvProductPriceBottomSheet.text =
                getString(R.string.currency_dollar, product.price)

            val shimmerDrawable = ShimmerDrawable().apply {
                setShimmer(shimmer)
            }
            Glide.with(this@AddToCartBottomSheetFragment)
                .load(product.images.firstOrNull())
                .placeholder(shimmerDrawable)
                .error(R.drawable.ic_image_24)
                .into(binding.ivProductImageBottomSheet)
        } else {
            dismiss()
        }

        binding.btnCloseIcon.setOnClickListener {
            dismiss()
        }

        binding.btnDecreaseQty.setOnClickListener {
            viewModel.decreaseQuantity()
        }

        binding.btnIncreaseQty.setOnClickListener {
            viewModel.increaseQuantity()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.quantity.collect { quantity ->
                        binding.tvProductQuantity.text = quantity.toString()
                    }
                }

                launch {
                    viewModel.addToCartState.collect { resource ->
                        when(resource) {
                            is Resource.Loading -> {
                                binding.progressBar.isVisible = true
                            }
                            is Resource.Success -> {
                                binding.progressBar.isVisible = false
                                val resultBundle = Bundle().apply {
                                    putBoolean(IS_SUCCESS, true)
                                }

                                setFragmentResult(ADD_CART_REQ, resultBundle)
                                viewModel.clearAddToCartState()
                                dismiss()
                            }
                            is Resource.Error -> {
                                binding.progressBar.isVisible = false
                                viewModel.clearAddToCartState()
                                if (resource.message == "ERROR_USER_NOT_FOUND"){
                                    requireContext().showGenericDialog(
                                        title = getString(R.string.error_login_required),
                                        message = getString(R.string.error_login_required_desc),
                                        positiveButtonText = getString(R.string.sign_in),
                                        negativeButtonText = getString(R.string.cancel),
                                        isCancelable = true,
                                        onPositiveClick = {
                                            val uri = "app://ecommerce/account".toUri()

                                            val navOptions = navOptions {
                                                popUpTo(findNavController().graph.startDestinationId){
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                            findNavController().navigate(uri, navOptions)
                                        }
                                    )
                                }
                            }
                            null -> {
                                binding.progressBar.isVisible = false
                            }
                        }
                    }
                }
            }
        }

        binding.btnAddToCart.setOnClickListener {
            if (product != null){
                viewModel.onAddToCartClicked(product)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val BOTTOM_SHEET_TAG = "AddToCartBottomSheet"
        const val PRODUCT_ARG = "product"
        const val ADD_CART_REQ = "add_cart_request"
        const val IS_SUCCESS = "is_success"
    }
}