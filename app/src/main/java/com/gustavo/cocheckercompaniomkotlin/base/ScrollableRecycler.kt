package com.gustavo.cocheckercompaniomkotlin.base

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class MyCustomRecycler(context: Context, attrs: AttributeSet?,  defStyleAttr:Int): RecyclerView(context,attrs, defStyleAttr) {
    var loadMore: (()-> Unit)? = null

    fun addLodMoreFunction(newLoad :() -> Unit){
        loadMore = newLoad
    }

    init{
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    if(loadMore!= null){
                        loadMore
                    }
                }
            }
        })
    }
}