package com.kevinfreyap.search.ui

import android.content.res.ColorStateList
import android.graphics.Color
import java.util.Currency
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.slider.RangeSlider
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.ProductCategory
import com.kevinfreyap.core.utils.dpToPx
import com.kevinfreyap.search.R
import com.kevinfreyap.shared_ui.R as sharedR
import com.kevinfreyap.search.databinding.BottomSheetFragmentSearchFilterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat

@AndroidEntryPoint
class SearchFilterBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: SearchViewModel by activityViewModels()

    private var _binding: BottomSheetFragmentSearchFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = BottomSheetFragmentSearchFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPriceSlider()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.categories.collect { resource ->
                        if (resource is Resource.Success) {
                            setupCategories(resource.data)
                        }
                    }
                }

                launch {
                    viewModel.filterState.collect { filter ->

                        val min = filter.minPrice?.toFloat() ?: binding.priceSlider.valueFrom
                        val max = filter.maxPrice?.toFloat() ?: binding.priceSlider.valueTo

                        binding.priceSlider.values = listOf(min, max)
                        binding.tvPriceRangeValue.text = getString(sharedR.string.price_range_value, min.toInt(), max.toInt())
                    }
                }
            }
        }

        binding.priceSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(p0: RangeSlider) {}

            override fun onStopTrackingTouch(slider: RangeSlider) {
                val values = slider.values
                val min = values[0].toInt()
                val max = values[1].toInt()

                viewModel.setMinPrice(min)
                viewModel.setMaxPrice(max)
            }
        })

        binding.btnClear.setOnClickListener {
            viewModel.onResetFilter()
            dismiss()
        }

        binding.btnApply.setOnClickListener {
            viewModel.onApplyFilter()
            dismiss()
        }
    }

    private fun setupCategories(categories: List<ProductCategory>) {
        binding.chipGroupCategory.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        val currentSelected = viewModel.filterState.value.category

        categories.forEach { category ->
            val chip = inflater.inflate(
                R.layout.item_filter_chip,
                binding.chipGroupCategory,
                false
            ) as Chip

            chip.text = category.name
            if (category.name == currentSelected) {
                chip.isChecked = true
            }

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.onCategorySelected(category.name)
                } else {
                    viewModel.onCategorySelected(null)
                }
            }

            binding.chipGroupCategory.addView(chip)
        }
    }

    private fun setupPriceSlider() {
        val defaultStart = 0
        val defaultEnd = 1000

        binding.tvPriceRangeValue.text = getString(sharedR.string.price_range_value, defaultStart, defaultEnd)
        binding.priceSlider.apply {
            valueFrom = defaultStart.toFloat()
            valueTo = defaultEnd.toFloat()
            stepSize = 20f

            tickInactiveTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            tickActiveTintList = ColorStateList.valueOf(Color.TRANSPARENT)

            val grey = ContextCompat.getColor(context, sharedR.color.grey_300) // Your #E0E0E0 color
            val darkGrey = ContextCompat.getColor(context, sharedR.color.grey_600)

            // Apply using ColorStateList
            trackInactiveTintList = ColorStateList.valueOf(darkGrey)

            thumbRadius = 12.dpToPx
            haloRadius = 24.dpToPx
            thumbElevation = 4f.dpToPx
            thumbTintList = ColorStateList.valueOf(grey)
        }

        binding.priceSlider.setLabelFormatter { value ->
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            format.currency = Currency.getInstance("USD")
            format.format(value)
        }

        binding.priceSlider.addOnChangeListener { slider, value, fromUser ->
            val values = slider.values
            val min = values[0].toInt()
            val max = values[1].toInt()

            binding.tvPriceRangeValue.text = getString(sharedR.string.price_range_value, min, max)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SEARCH_FILTER_BOTTOM_SHEET = "search_filter_bottom_sheet"
    }
}