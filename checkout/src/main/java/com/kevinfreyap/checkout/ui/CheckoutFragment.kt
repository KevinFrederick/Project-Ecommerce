package com.kevinfreyap.checkout.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.kevinfreyap.checkout.databinding.FragmentCheckoutBinding
import com.kevinfreyap.checkout.utils.OrderState
import com.kevinfreyap.core.utils.PaymentMethod
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.shared_ui.adapter.CartAdapter
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutFragment : Fragment() {
    private val viewModel: CheckoutViewModel by viewModels()

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: SwipeRecyclerView
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.rvCart
        setupRecyclerView()

        binding.codOption.setOnClickListener {
            viewModel.selectMethod(PaymentMethod.CASH)
        }

        binding.cardOption.setOnClickListener {
            // viewModel.selectMethod(PaymentMethod.CARD)
        }

        binding.tvApplyBtn.setOnClickListener {
            val voucherCode = binding.etVoucherInput.text.toString()

            if (voucherCode.isNotEmpty()) {
                // TODO (Apply Voucher Functionality)
            } else {
                binding.etVoucherInput.error = getString(R.string.error_no_voucher)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userAddress.collect { address ->
                        if (address != null) {
                            binding.tvShippingAddress.text = address.toString()
                        } else {
                            binding.tvShippingAddress.text = getString(R.string.text_no_address)
                        }
                    }
                }

                launch {
                    viewModel.selectedMethod.collect { method ->
                        updateSelectionUI(method)
                    }
                }

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

                        binding.tvVoucherDiscount.text = getString(R.string.minus_currency_dollar, summary.voucherDiscount)

                        binding.tvTotalPrice.text = getString(R.string.currency_dollar, summary.total)

                        binding.btnOrder.isEnabled = summary.total > 0
                    }
                }

                launch {
                    viewModel.orderState.collect { state ->
                        binding.btnOrder.isEnabled = state !is OrderState.Loading
                        binding.progressBar.isVisible = state is OrderState.Loading

                        if (state is OrderState.OrderSuccess){
                            showOrderSuccessDialog(state.receipt.orderId)
                            viewModel.resetOrderState()
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
                                "ERROR_EMPTY_CART" -> {
                                    findNavController().navigateUp()
                                    getString(R.string.error_empty_cart)
                                }
                                "ERROR_NO_ADDRESS" -> {
                                    getString(R.string.error_no_address)
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

        binding.btnOrder.setOnClickListener {
            viewModel.onOrderClicked()
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
            onNavigation = {},
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

    private fun updateSelectionUI(selectedMethod: PaymentMethod) {
        when(selectedMethod) {
            PaymentMethod.CASH -> {
                binding.ivCodSelector.setImageResource(R.drawable.ic_radio_button_checked_24)
                binding.ivCardSelector.setImageResource(R.drawable.ic_radio_button_unchecked_24)
            }

            PaymentMethod.CARD -> {
                binding.ivCodSelector.setImageResource(R.drawable.ic_radio_button_unchecked_24)
                binding.ivCardSelector.setImageResource(R.drawable.ic_radio_button_checked_24)
            }
        }
    }

    private fun showOrderSuccessDialog(orderId: String) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.success_order_placed))
            .setMessage(getString(R.string.order_success_desc, orderId))
            .setCancelable(true) // Allow outside click and back press
            .create()

        val job = viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)

            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }

        dialog.setOnDismissListener {
            job.cancel()
            navigateToHome()
        }

        dialog.show()
    }

    private fun navigateToHome() {
        if (!isAdded) return
        
        val uri = "app://ecommerce/home".toUri()

        val navOptions = navOptions {
            popUpTo(findNavController().graph.findStartDestination().id){
                inclusive = true
            }
        }
        findNavController().navigate(uri, navOptions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}