package com.gustavo.cocheckercompaniomkotlin.ui.measure

import android.annotation.SuppressLint
import android.icu.util.Measure
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gustavo.cocheckercompaniomkotlin.base.BaseAdapter
import com.gustavo.cocheckercompaniomkotlin.model.data.MeasureItem
import com.gustavo.cocheckercompaniomkotlin.ui.measure.MeasureListAdapter.MeasureViewHolder
import com.gustavo.cocheckercompaniomkotlin.utils.safeRun
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.MeasureItemBinding

class MeasureListAdapter() : BaseAdapter<MeasureViewHolder,MeasureItem>() {
    val dataList: MutableList<MeasureItem>  = mutableListOf()

    fun setCurrentData(newDataList: List<MeasureItem>) {
        dataList.clear()
        dataList.addAll(newDataList)
        notifyDataSetChanged()
    }

    fun addData(data:MeasureItem){
        dataList.add(data)
        notifyItemInserted(dataList.lastIndex)
    }

    fun isDataEmpty():Boolean{
        return dataList.isEmpty()
    }

    fun addAll(newDataList : List<MeasureItem>){
        val lastIndex = dataList.lastIndex
        dataList.addAll(newDataList)
        notifyItemRangeInserted(lastIndex,dataList.lastIndex)
    }

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
                R.layout.measure_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = dataList.size

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