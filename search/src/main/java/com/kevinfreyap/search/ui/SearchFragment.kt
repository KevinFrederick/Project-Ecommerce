package com.kevinfreyap.search.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kevinfreyap.search.databinding.FragmentSearchBinding
import com.kevinfreyap.shared_ui.adapter.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var imm: InputMethodManager
    private var currentLoadState: CombinedLoadStates? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.rvSearchResult
        setupRecyclerView()

        binding.etSearch.requestFocus()

        imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch.editText, InputMethodManager.SHOW_IMPLICIT)

        binding.etSearch.editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString()
                handleQuery(query)
                true
            } else {
                false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchResult.collectLatest { data ->
                        productAdapter.submitData(data)
                    }
                }
            }
        }

        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.getText()
            handleQuery(query)
        }

        productAdapter.addLoadStateListener { loadStates ->
            currentLoadState = loadStates

            updateSearchUi()
        }
        updateSearchUi()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter{ productId ->
            val uri = "app://ecommerce/product/${productId}".toUri()
            val navOptions = navOptions{
                popUpTo(com.kevinfreyap.search.R.id.searchFragment) {
                    inclusive = true
                }
            }
            findNavController().navigate(
                uri,
                navOptions
            )
        }

        recyclerView.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun handleQuery(query: String) {
        viewModel.onQueryChange(query)
        if (query.isBlank()){
            updateSearchUi()
        }

        hideKeyboard()
    }

    private fun hideKeyboard() {
        val currentView = activity?.currentFocus ?: view ?: View(requireContext())
        imm.hideSoftInputFromWindow(currentView.windowToken, 0)
        binding.etSearch.clearFocus()
    }

    private fun updateSearchUi() {
        val loadState = currentLoadState

        val isQueryEmpty = binding.etSearch.getText().isEmpty()

        val isMediatorLoading = loadState?.mediator?.refresh is LoadState.Loading
        val isSourceLoading = loadState?.source?.refresh is LoadState.Loading
        val isTrulyLoading = isMediatorLoading || isSourceLoading

        val hasItems = productAdapter.itemCount > 0

        binding.tvStartSearching.isVisible = isQueryEmpty

        binding.tvNoItemFound.isVisible = !isQueryEmpty && !isTrulyLoading && !hasItems

        recyclerView.isVisible = !isQueryEmpty && hasItems
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}