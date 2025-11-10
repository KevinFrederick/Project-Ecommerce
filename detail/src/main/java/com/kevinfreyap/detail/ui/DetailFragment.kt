package com.kevinfreyap.detail.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

                        val product = resource.data
                        if (product != null){
                            carouselAdapter.submitList(product.images)
                            binding.tvProductName.text = product.title
                            binding.tvProductPrice.text = getString(R.string.currency_dollar, product.price)
                            binding.tvProductDescription.text = product.description
                        }

                        if (resource is Resource.Error) {
                            val message = when(resource.message) {
                                "ERROR_PRODUCT_UNAVAILABLE" -> {
                                    getString(R.string.error_unavailable_product)
                                }
                                else -> {
                                    Log.e("DetailFragment", resource.message.toString())
                                    getString(R.string.error_unknown)
                                }
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                            if (product == null) {
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