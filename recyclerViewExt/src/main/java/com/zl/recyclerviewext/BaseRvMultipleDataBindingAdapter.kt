package com.zl.recyclerviewext

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


/**
 *  @author : ling.zhang
 *  date : 4/14/21 1:34 PM
 *  description : 适配器基类
 */
open class BaseRvMultipleDataBindingAdapter :
    BaseRvAdapter<Any, BaseRvMultipleDataBindingAdapter.VH>() {
    private val itemTypeMap =
        mutableMapOf<Class<out Any>, RecyclerViewItemType<out Any, out ViewDataBinding>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val key =
            itemTypeMap.keys.find { it.hashCode() == viewType }

        return VH(
            (itemTypeMap[key])?.getDataBinding(parent)
                ?: kotlin.run { throw RuntimeException("未找到匹配的类型") })
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        itemClickListener?.let {
            holder.dataBinding.root.setOnClickListener { view ->
                itemClickListener?.invoke(view, data!![position], position)
            }
        }
        itemLongClickListener?.let {
            holder.dataBinding.root.setOnLongClickListener { view ->
                itemLongClickListener?.invoke(view, data!![position], position) ?: false
            }
        }
        val dataCls = data!![position].javaClass
        val recyclerViewItemType = itemTypeMap[dataCls]
        recyclerViewItemType?.bindData(holder.dataBinding, data!![position], position)
        holder.dataBinding.executePendingBindings()
    }

    override fun getItemViewType(position: Int): Int {
        val data = data!![position]
        for (klass in itemTypeMap.keys) {
            if (klass.isAssignableFrom(data.javaClass)) {
                return klass.hashCode()
            }
        }
        return -1
    }

    fun <T : Any, DBINDING : ViewDataBinding> registerItemLayout(
        context: Context,
        layoutRes: Int,
        clazz: Class<T>,
        bindData: (dBinding: DBINDING, data: T, position: Int) -> Unit
    ) {
        val recyclerViewItemType = object : RecyclerViewItemType<T, DBINDING>(bindData) {
            override fun getDataBinding(parent: ViewGroup): DBINDING {
                return DataBindingUtil.inflate(
                    LayoutInflater.from(context), layoutRes, parent, false
                )
            }
        }
        itemTypeMap[clazz] = recyclerViewItemType
    }

    inner class VH(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)
}