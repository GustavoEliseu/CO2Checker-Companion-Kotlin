package com.gustavo.cocheckercompaniomkotlin.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.gustavo.cocheckercompaniomkotlin.model.data.GenericData
import com.gustavo.cocheckercompaniomkotlin.utils.safeRun

abstract class BaseAdapter<T: BaseAdapter.BaseViewHolder,E: GenericData>() : RecyclerView.Adapter<BaseAdapter.BaseViewHolder>() {

//    fun setCurrentData(newDataList: List<E>) {
//        dataList.clear()
//        dataList.addAll(newDataList)
//        notifyDataSetChanged()
//    }
//
//    fun addData(data:E){
//        dataList.add(data)
//        notifyItemInserted(dataList.lastIndex)
//    }
//
//    fun isDataEmpty():Boolean{
//        return dataList.isEmpty()
//    }
//
//    fun addAll(newDataList : List<E>){
//        val lastIndex = dataList.lastIndex
//        dataList.addAll(newDataList)
//        notifyItemRangeInserted(lastIndex,dataList.lastIndex)
//    }

    abstract class BaseViewHolder(view: View):RecyclerView.ViewHolder(view)
}