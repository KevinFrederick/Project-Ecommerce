package com.kevinfreyap.cart.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kevinfreyap.cart.adapter.CartAdapter
import com.kevinfreyap.cart.databinding.FragmentCartBinding
import com.kevinfreyap.cart.utils.CheckoutActionState
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_ui.R
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {
    private val viewModel: CartViewModel by viewModels()

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: SwipeRecyclerView
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.rvCart
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.cartList.collect { resource ->
                        when(resource) {
                            is Resource.Loading -> {
                                binding.progressBar.isVisible = true
                            }
                            is Resource.Success -> {
                                binding.progressBar.isVisible = false
                                cartAdapter.submitList(resource.data)
                            }
                            is Resource.Error -> {
                                binding.progressBar.isVisible = false
                                if (resource.message == "ERROR_USER_NOT_FOUND") {
                                    binding.cartLayout.isVisible = false
                                    binding.noItemLayout.isVisible = true
                                } else {
                                    binding.cartLayout.isVisible = true
                                    binding.noItemLayout.isVisible = false
                                }
                                val message = when(resource.message) {
                                    "ERROR_USER_NOT_FOUND" -> {
                                        getString(R.string.error_user_not_found)
                                    }
                                    "ERROR_FAILED_TO_LOAD" -> {
                                        getString(R.string.error_failed_to_load)
                                    }
                                    "ERROR_NO_CONNECTION" -> {
                                        getString(R.string.error_no_connection)
                                    }
                                    else -> {
                                        Log.e("CartFragment", resource.message.toString())
                                        getString(R.string.error_unknown)
                                    }
                                }
                                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.cartSummary.collect { summary ->
                        binding.tvSubtotalPrice.text = getString(R.string.currency_dollar, summary.subtotal)

                        if (summary.shippingFee == 0) {
                            binding.tvShippingFee.text = getString(R.string.free)
                        } else {
                            binding.tvShippingFee.text = getString(R.string.currency_dollar, summary.shippingFee)
                        }

                        binding.tvTotalPrice.text = getString(R.string.currency_dollar, summary.total)

                        binding.btnCheckout.isEnabled = summary.total > 0
                    }
                }

                launch {
                    viewModel.checkoutState.collect { state ->
                        binding.btnCheckout.isEnabled = state !is CheckoutActionState.Loading
                        binding.progressBar.isVisible = state is CheckoutActionState.Loading

                        if (state is CheckoutActionState.Navigate) {
                            // TODO (Navigate to Checkout Page)
                            viewModel.resetCheckoutState()
                        }
                    }
                }

                launch {
                    viewModel.errorState.collect { errorMessage ->
                        if (errorMessage != null) {
                            val message = when(errorMessage) {
                                "ERROR_USER_NOT_FOUND" -> {
                                    getString(R.string.error_user_not_found)
                                }
                                "ERROR_NO_CONNECTION" -> {
                                    getString(R.string.error_no_connection)
                                }
                                "ERROR_REMOVE_UNAVAILABLE_ITEM" -> {
                                    getString(R.string.error_remove_unavailable_item)
                                }
                                else -> {
                                    Log.e("CartFragment", errorMessage)
                                    getString(R.string.error_unknown)
                                }
                            }
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()

                            viewModel.clearError()
                        }
                    }
                }
            }
        }

        binding.btnReturn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCheckout.setOnClickListener {
            viewModel.onCheckoutClicked()
        }
    }

    private fun setupRecyclerView() {
        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val deleteItem = SwipeMenuItem(context)
                .setBackground(R.color.red_500)
                .setImage(R.drawable.ic_delete_24)
                .setWidth(250)
                .setHeight(MATCH_PARENT)

            swipeRightMenu.addMenuItem(deleteItem)
        }

        recyclerView.setSwipeMenuCreator(swipeMenuCreator)
        recyclerView.setOnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val cartItem = cartAdapter.currentList[adapterPosition]
                cartItem?.let {
                    viewModel.removeItemFromCart(it)
                }
            }
        }

        cartAdapter = CartAdapter(
            onNavigation = {
                val uri = "app://ecommerce/product/${it.product.id}".toUri()
                val navOptions = navOptions {
                    popUpTo(com.kevinfreyap.cart.R.id.cartFragment) {
                        inclusive = true
                    }
                }

                findNavController().navigate(
                    uri, navOptions
                )
            },
            onIncrease = {
                viewModel.increaseQuantity(it)
            },
            onDecrease = {
                viewModel.decreaseQuantity(it)
            }
        )
        recyclerView.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}