package com.kevinfreyap.wishlist.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.wishlist.adapter.WishlistAdapter
import com.kevinfreyap.wishlist.databinding.FragmentWishlistBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishlistFragment : Fragment() {
    private val viewModel: WishlistViewModel by viewModels()

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var wishlistAdapter: WishlistAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.rvWishlist
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.wishlist.collect { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading

                        if (resource is Resource.Success) {
                            if (resource.data.isEmpty()) {
                                binding.tvNoWishlist.isVisible = true
                            } else {
                                binding.tvNoWishlist.isVisible = false
                                wishlistAdapter.submitList(resource.data)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        wishlistAdapter = WishlistAdapter(
            onItemClick = { productId ->
                val uri = "app://ecommerce/product/${productId}".toUri()
                val navOptions = navOptions {
                    popUpTo(findNavController().graph.findStartDestination().id){
                        inclusive = false
                    }
                }
                findNavController().navigate(uri, navOptions)
            },
            onWishlistClick = { productId ->
                viewModel.onRemoveWishlist(productId)
            }
        )
        recyclerView.apply {
            adapter = wishlistAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}