package com.zl.recyclerviewext

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding

/**
 *  @author : ling.zhang
 *  date : 2022/7/15 14:18
 *  description :
 */
abstract class RecyclerViewItemType<T, DBINDING : ViewDataBinding>(val bindData: (dBinding: DBINDING, data: T, position: Int) -> Unit) {
    fun bindData(dBinding: ViewDataBinding, data: Any, position: Int) {
        (dBinding as? DBINDING).let {
            (data as? T).let {
                bindData.invoke(dBinding as DBINDING, data as T, position)
            }
        }
    }

    abstract fun getDataBinding(viewGroup: ViewGroup): DBINDING
}