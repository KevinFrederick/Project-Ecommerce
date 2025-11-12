package com.kevinfreyap.detail.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.detail.databinding.BottomSheetFragmentAddToCartBinding
import com.kevinfreyap.shared_ui.R
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
                    viewModel.productState.collect { resource ->
                        if (resource is Resource.Success){
                            val product = resource.data
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
                                val message = when (resource.message) {
                                    "ERROR_PRODUCT_UNAVAILABLE" -> {
                                        getString(R.string.error_unavailable_product)
                                    }

                                    else -> {
                                        Log.e(TAG, resource.message.toString())
                                        getString(R.string.error_unknown)
                                    }
                                }

                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        } else if (resource is Resource.Error) {
                            Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

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
                                Toast.makeText(context, getString(R.string.success_add_to_cart), Toast.LENGTH_SHORT).show()
                                viewModel.clearAddToCartState()
                                dismiss()
                            }
                            is Resource.Error -> {
                                binding.progressBar.isVisible = false
                                viewModel.clearAddToCartState()
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
            viewModel.onAddToCartClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddToCartBottomSheet"
    }
}