package com.kevinfreyap.transaction.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.utils.PaymentMethod
import com.kevinfreyap.core.utils.TimeUtils
import com.kevinfreyap.shared_ui.R
import com.kevinfreyap.transaction.adapter.OrderItemAdapter
import com.kevinfreyap.transaction.databinding.FragmentTransactionDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionDetailFragment : Fragment() {
    private val viewModel: TransactionDetailViewModel by viewModels()
    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderItemAdapter: OrderItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.rvOrderedItems
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.transaction.collect { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading

                        if (resource is Resource.Success) {
                            val transaction = resource.data
                            binding.tvTransactionId.text = getString(R.string.transaction_id, transaction.orderId)
                            binding.tvOrderDate.text = TimeUtils.formatTransactionTime(transaction.datePlaced)
                            binding.tvShippingAddress.text = transaction.shippingAddress.toString()
                            binding.tvPaymentMethod.text = when(transaction.paymentMethod) {
                                PaymentMethod.CASH -> getString(R.string.cash_on_delivery)
                                PaymentMethod.CARD -> getString(R.string.debit_credit_card)
                            }

                            orderItemAdapter.submitList(transaction.itemsPurchased)

                            binding.tvSubtotal.text = getString(R.string.currency_dollar, transaction.subtotal)
                            binding.tvShippingFee.text = if (transaction.shippingFee == 0) {
                                getString(R.string.free)
                            } else {
                                getString(R.string.currency_dollar, transaction.shippingFee)
                            }
                            binding.tvVoucher.text = getString(R.string.currency_dollar, transaction.discountAmount)
                            binding.tvTotal.text = getString(R.string.currency_dollar, transaction.totalPaid)
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        orderItemAdapter = OrderItemAdapter()
        recyclerView.apply {
            adapter = orderItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}