package com.kevinfreyap.voucher.ui

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
import com.kevinfreyap.voucher.adapter.VoucherAdapter
import com.kevinfreyap.voucher.databinding.FragmentVoucherBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VoucherFragment : Fragment() {
    private val viewModel: VoucherViewModel by viewModels()

    private var _binding: FragmentVoucherBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var voucherAdapter: VoucherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.rvVoucher
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.voucherList.collect { resource ->
                        binding.progressBar.isVisible = resource is Resource.Loading

                        if (resource is Resource.Success) {
                            binding.tvNoVoucher.isVisible = resource.data.isEmpty()
                            voucherAdapter.submitList(resource.data)
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        voucherAdapter = VoucherAdapter()
        recyclerView.apply {
            adapter = voucherAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.markAllAsSeen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}