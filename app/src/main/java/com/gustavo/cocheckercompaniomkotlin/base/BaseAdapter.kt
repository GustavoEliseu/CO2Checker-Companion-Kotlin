package com.gustavo.cocheckercompaniomkotlin.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.gustavo.cocheckercompaniomkotlin.model.data.GenericData
import com.gustavo.cocheckercompaniomkotlin.utils.safeRun

abstract class BaseAdapter<T: BaseAdapter.BaseViewHolder,E: GenericData>(loadMore: ()-> Unit) : RecyclerView.Adapter<BaseAdapter.BaseViewHolder>() {
    abstract val dataList : MutableList<E>
    fun setCurrentData(newDataList: List<E>) {
        dataList.clear()
        dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun addData(data:E){
        dataList.add(data)
        notifyItemInserted(dataList.lastIndex)
    }

    fun addAll(newDataList : List<E>){
        val lastIndex = dataList.lastIndex
        dataList.addAll(newDataList)
        notifyItemRangeInserted(lastIndex,dataList.lastIndex)
    }

    fun removeData(data: E) {
        safeRun {
            val index = dataList.indexOf(data)
            dataList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = dataList.size
    abstract class BaseViewHolder(view: View):RecyclerView.ViewHolder(view)
}