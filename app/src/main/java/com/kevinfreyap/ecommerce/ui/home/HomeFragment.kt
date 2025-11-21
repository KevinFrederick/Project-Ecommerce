package com.kevinfreyap.ecommerce.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kevinfreyap.ecommerce.databinding.FragmentHomeBinding
import com.kevinfreyap.core.data.source.MainViewModel
import com.kevinfreyap.shared_ui.adapter.ProductAdapter
import com.kevinfreyap.shared_ui.util.setupCartMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.rvHomeProducts
        setupRecyclerview()
        setupCartMenu(
            cartItemCount = mainViewModel.cartItemCount
        ) {
            val uri = "app://ecommerce/cart".toUri()
            findNavController().navigate(uri)
        }

        binding.btnSearchFake.setOnClickListener{
            val uri = "app://ecommerce/search".toUri()
            findNavController().navigate(uri)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.isNetworkAvailable
                        .drop(1)
                        .collect { isAvailable ->
                            if (isAvailable) {
                                productAdapter.refresh()
                            }
                        }
                }
                launch {
                    viewModel.productList.collectLatest { pagingData ->
                        productAdapter.submitData(pagingData)
                    }
                }

                launch {
                    productAdapter.loadStateFlow.collectLatest { loadStates ->
                        // Get the state for "refresh" (full-page load)
                        val refreshState = loadStates.refresh

                        // Show the *linear* progress bar only on the very first load
                        binding.progressBar.isVisible =
                            refreshState is LoadState.Loading && productAdapter.itemCount == 0

                        // Show the *swipe* spinner only when swiping to refresh
                        binding.swipeRefreshLayout.isRefreshing =
                            refreshState is LoadState.Loading && productAdapter.itemCount > 0
                    }
                }
            }
        }
    }

    private fun setupRecyclerview() {
        productAdapter = ProductAdapter{ productId ->
            val uri = "app://ecommerce/product/${productId}".toUri()
            findNavController().navigate(
                uri
            )
        }
        recyclerView.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            productAdapter.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}