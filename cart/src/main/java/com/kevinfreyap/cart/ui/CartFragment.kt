package com.kevinfreyap.cart.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kevinfreyap.cart.adapter.CartAdapter
import com.kevinfreyap.cart.databinding.FragmentCartBinding
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_ui.R
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    // Get the CartViewModel using activityViewModels()
    // This shares the ViewModel across all fragments.
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
                                Log.d("Cart", resource.data.size.toString())
                            }
                            is Resource.Error -> {
                                binding.progressBar.isVisible = false
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
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
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
                                else -> {
                                    Log.e("CartFragment", errorMessage)
                                    getString(R.string.error_unknown)
                                }
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                            viewModel.clearError()
                        }
                    }
                }
            }
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