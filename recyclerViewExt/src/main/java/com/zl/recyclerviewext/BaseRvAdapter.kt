package com.zl.recyclerviewext

import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 *  @author : ling.zhang
 *  date : 2022/6/18 20:43
 *  description :
 */
abstract class BaseRvAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    var page = 1
    var lastId = ""
    var data: MutableList<T>? = null
    var itemClickListener: ((view: View, data: T, position: Int) -> Unit)? = null
    var itemLongClickListener: ((view: View, data: T, position: Int) -> Boolean)? = null

    open fun refreshData(datas: Collection<T>) {
        this.data = data ?: mutableListOf()
        this.data!!.clear()
        this.data!!.addAll(datas)
        notifyDataSetChanged()
    }

    open fun addData(datas: Collection<T>) {
        this.data = data ?: mutableListOf()
        this.data!!.addAll(datas)
        notifyItemRangeChanged(this.data!!.size - datas.size, datas.size)
    }

    open fun insertData(datas: Collection<T>, index: Int) {
        this.data = data ?: mutableListOf()
        this.data!!.addAll(index, datas)
        notifyDataSetChanged()
    }

    open fun refreshSingleData(index: Int, d: T) {
        this.data = data ?: mutableListOf()
        data!![index] = d
        notifyItemChanged(index)
    }

    open fun addData(d: T) {
        this.data = data ?: mutableListOf()
        this.data!!.add(d)
        notifyItemRangeChanged(this.data!!.size - 1, 1)
    }

    open fun removeData(d: T) {
        this.data = data ?: mutableListOf()
        if (this.data!!.contains(d)) {
            this.data!!.remove(d)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
}