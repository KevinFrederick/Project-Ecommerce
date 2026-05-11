package com.kevinfreyap.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_product.domain.model.SearchFilter
import com.kevinfreyap.shared_product.domain.usecase.GetCategoriesUseCase
import com.kevinfreyap.shared_product.domain.usecase.GetProductUseCase
import com.kevinfreyap.shared_product.domain.usecase.RefreshCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    getCategories: GetCategoriesUseCase,
    private val getProducts: GetProductUseCase,
    private val refreshCategories: RefreshCategoriesUseCase
): ViewModel(){
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val categories = getCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    private val _selectedCategory = MutableStateFlow<String?>(null)

    private val _minPrice = MutableStateFlow<Int?>(null)
    private val _maxPrice = MutableStateFlow<Int?>(null)

    private val _filterState = MutableStateFlow(SearchFilter())
    val filterState: StateFlow<SearchFilter> = _filterState

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResult = combine(
        flow = _searchQuery,
        flow2 = _filterState
    ) { query, filter ->
        Pair(query, filter)
    }
        .flatMapLatest { (query, filter) ->
            val isQueryEmpty = query.isBlank()
            val hasFilter = !filter.category.isNullOrEmpty() ||
                    filter.minPrice != null ||
                    filter.maxPrice != null

            if (isQueryEmpty && !hasFilter) {
                flowOf(PagingData.empty())
            } else {
                getProducts(query, filter)
            }
        }
        .cachedIn(viewModelScope)

    init {
        syncCategory()
    }

    fun syncCategory(){
        viewModelScope.launch(Dispatchers.IO) {
            refreshCategories()
        }
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }

    fun setMinPrice(price: Int) {
        _minPrice.value = price
    }

    fun setMaxPrice(price: Int) {
        _maxPrice.value = price
    }

    fun onApplyFilter() {
        val searchFilter = SearchFilter(
            minPrice = _minPrice.value,
            maxPrice = _maxPrice.value,
            category = _selectedCategory.value
        )
        _filterState.value = searchFilter
    }

    fun onResetFilter() {
        _filterState.value = SearchFilter()
        _minPrice.value = null
        _maxPrice.value = null
        _selectedCategory.value = null
    }
}