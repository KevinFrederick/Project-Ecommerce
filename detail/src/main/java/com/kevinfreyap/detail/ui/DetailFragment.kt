package com.kevinfreyap.detail.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.source.MainViewModel
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.utils.isOnline
import com.kevinfreyap.detail.ImageCarouselAdapter
import com.kevinfreyap.detail.databinding.FragmentDetailBinding
import com.kevinfreyap.detail.ui.AddToCartBottomSheetFragment.Companion.ADD_CART_REQ
import com.kevinfreyap.detail.ui.AddToCartBottomSheetFragment.Companion.BOTTOM_SHEET_TAG
import com.kevinfreyap.detail.ui.AddToCartBottomSheetFragment.Companion.IS_SUCCESS
import com.kevinfreyap.detail.ui.AddToCartBottomSheetFragment.Companion.PRODUCT_ARG
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.shared_ui.util.setupCartMenu
import com.kevinfreyap.shared_ui.util.showGenericDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: DetailViewModel by viewModels()

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val carouselAdapter = ImageCarouselAdapter()
    private var currentProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartMenu (
            cartItemCount = mainViewModel.cartItemCount
        )  {
            val uri = "app://ecommerce/cart".toUri()
            findNavController().navigate(uri)
        }

        childFragmentManager.setFragmentResultListener(ADD_CART_REQ, viewLifecycleOwner) {requestKey, bundle ->
            val isSuccess = bundle.getBoolean(IS_SUCCESS)
            if (isSuccess) {
                showCartSnackBar()
            }
        }

        binding.viewPagerImageCarousel.adapter = carouselAdapter
        TabLayoutMediator(
            binding.tabLayoutImageCarouselDots,
            binding.viewPagerImageCarousel
        ) { tab, position -> }.attach()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productState.collect { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading

                        resource.data?.let { product ->
                            currentProduct = product

                            with(binding) {
                                carouselAdapter.submitList(product.images)
                                tvProductName.text = product.title
                                tvProductPrice.text = getString(R.string.currency_dollar,
                                    product.price
                                )
                                tvProductDescription.text = product.description
                            }
                        }

                        if (resource is Resource.Error) {
                            when(resource.message) {
                                "ERROR_PRODUCT_UNAVAILABLE" -> {
                                    requireContext().showGenericDialog(
                                        title = getString(R.string.error_unavailable_product),
                                        message = getString(R.string.error_no_product_desc),
                                        positiveButtonText = getString(R.string.back),
                                        isCancelable = false,
                                        onPositiveClick = { findNavController().navigateUp() }
                                    )
                                }

                                else -> {
                                    Log.e("DetailFragment", resource.message.toString())
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.isInWishlist.collect { value ->
                        if (value) {
                            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_24)
                        } else {
                            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border_24)
                        }
                    }
                }

                launch {
                    viewModel.wishlistState.collect { resource ->
                        if (resource is Resource.Error) {
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
                    }
                }
            }
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.onClickWishlist()
        }

        binding.btnAddToCartSheet.setOnClickListener {
            currentProduct?.let { product ->
                val bottomSheetFragment = AddToCartBottomSheetFragment()
                val bundle = Bundle().apply {
                    putParcelable(PRODUCT_ARG, product)
                }

                bottomSheetFragment.arguments = bundle
                bottomSheetFragment.show(childFragmentManager, BOTTOM_SHEET_TAG)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showCartSnackBar() {
        val message = if (requireContext().isOnline()) {
            getString(R.string.success_add_to_cart)
        } else {
            getString(R.string.success_add_to_cart_offline)
        }

        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).setAnchorView(binding.btnAddToCartSheet).show()
    }

}