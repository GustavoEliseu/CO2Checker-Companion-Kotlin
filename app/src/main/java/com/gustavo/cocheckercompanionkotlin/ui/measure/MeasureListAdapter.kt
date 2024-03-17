package com.gustavo.cocheckercompanionkotlin.ui.measure

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gustavo.cocheckercompanionkotlin.base.BaseAdapter
import com.gustavo.cocheckercompanionkotlin.model.data.MeasureItem
import com.gustavo.cocheckercompanionkotlin.ui.measure.MeasureListAdapter.MeasureViewHolder
import com.gustavo.cocheckercompanionkotlin.utils.safeRun
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
            holder.itemView.setOnClickListener {
                dataList[position].expanded = !dataList[position].expanded
                notifyItemChanged(position)
            }
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