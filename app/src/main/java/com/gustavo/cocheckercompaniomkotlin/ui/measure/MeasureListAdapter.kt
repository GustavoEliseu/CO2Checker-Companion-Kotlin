package com.gustavo.cocheckercompaniomkotlin.ui.measure

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gustavo.cocheckercompaniomkotlin.base.BaseAdapter
import com.gustavo.cocheckercompaniomkotlin.model.data.MeasureItem
import com.gustavo.cocheckercompaniomkotlin.ui.measure.MeasureListAdapter.MeasureViewHolder
import com.gustavo.cocheckercompaniomkotlin.utils.safeRun
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.MeasureItemBinding

class MeasureListAdapter(private val loadMore: ()->Unit
) : BaseAdapter<MeasureViewHolder,MeasureItem>(loadMore) {
    override val dataList: MutableList<MeasureItem>  = mutableListOf()

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        safeRun {
            val measureData = dataList[position]
            (holder as? MeasureViewHolder)?.bind(measureData)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasureViewHolder {
        return MeasureViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.location_item,
                parent,
                false
            )
        )
    }

    class MeasureViewHolder(
        private val binding: MeasureItemBinding
    ) : BaseViewHolder(binding.root) {

        private val viewModel = MeasureItemViewModel()

        @SuppressLint("ClickableViewAccessibility")
        fun bind(measureItem: MeasureItem) {
            binding.viewModel = viewModel


            viewModel.bind(measureItem)
        }
    }
}