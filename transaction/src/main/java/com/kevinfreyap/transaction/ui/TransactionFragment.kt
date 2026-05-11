package com.kevinfreyap.transaction.ui

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_ui.util.setupCartMenu
import com.kevinfreyap.transaction.adapter.TransactionAdapter
import com.kevinfreyap.transaction.databinding.FragmentTransactionBinding
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class TransactionFragment : Fragment() {
    private val viewModel: TransactionViewModel by viewModels()

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: SwipeRecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerView
        setupRecyclerView()

        setupCartMenu(
            cartItemCount = viewModel.cartItemCount
        ) {
            val uri = "app://ecommerce/cart".toUri()
            findNavController().navigate(uri)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.transactionList.collect { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading

                        if (resource is Resource.Success){
                            if (resource.data.isEmpty()){
                                binding.tvNoTransaction.isVisible = true
                            }else {
                                binding.tvNoTransaction.isVisible = false
                                transactionAdapter.submitList(resource.data)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(){
        transactionAdapter = TransactionAdapter{ orderReceipt ->
            val uri = "app://ecommerce/transaction/${orderReceipt.orderId}".toUri()

            findNavController().navigate(uri)
        }
        recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}