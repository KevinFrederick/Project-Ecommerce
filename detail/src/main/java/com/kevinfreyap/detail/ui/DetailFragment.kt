package com.kevinfreyap.detail.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.detail.ImageCarouselAdapter
import com.kevinfreyap.detail.databinding.FragmentDetailBinding
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.shared_ui.util.setupCartMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private val viewModel: DetailViewModel by viewModels()

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val carouselAdapter = ImageCarouselAdapter()

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
            cartItemCount = viewModel.cartItemCount
        )  {
            val uri = "app://ecommerce/cart".toUri()
            findNavController().navigate(uri)
        }

        binding.viewPagerImageCarousel.adapter = carouselAdapter
        TabLayoutMediator(
            binding.tabLayoutImageCarouselDots,
            binding.viewPagerImageCarousel
        ) { tab, position -> }.attach()

        binding.btnAddToCartSheet.setOnClickListener {
            AddToCartBottomSheetFragment().show(childFragmentManager, AddToCartBottomSheetFragment.TAG)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productState.collect { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading

                        val product = resource.data
                        if (product != null){
                            carouselAdapter.submitList(product.images)
                            binding.tvProductName.text = product.title
                            binding.tvProductPrice.text = getString(R.string.currency_dollar, product.price)
                            binding.tvProductDescription.text = product.description
                        }

                        if (resource is Resource.Error) {
                            var shouldPopBack = false
                            val message = when(resource.message) {
                                "ERROR_USER_NOT_FOUND" -> {
                                    getString(R.string.error_user_not_found)
                                }
                                "ERROR_PRODUCT_UNAVAILABLE" -> {
                                    shouldPopBack = (product == null)
                                    getString(R.string.error_unavailable_product)
                                }
                                "ERROR_NO_CONNECTION" -> {
                                    getString(R.string.error_no_connection)
                                }
                                "ERROR_FAILED_TO_ADD_TO_CART" -> {
                                    getString(R.string.error_failed_to_add_to_cart)
                                }
                                else -> {
                                    Log.e("DetailFragment", resource.message.toString())
                                    getString(R.string.error_unknown)
                                }
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                            if (shouldPopBack) {
                                findNavController().popBackStack()
                            }
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