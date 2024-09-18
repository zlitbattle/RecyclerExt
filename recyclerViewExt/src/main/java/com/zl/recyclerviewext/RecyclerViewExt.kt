package com.zl.recyclerviewext

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout

inline val RecyclerView.orientation
    get() = if (layoutManager == null) -1 else layoutManager.run {
        when (this) {
            is GridLayoutManager -> 2
            is StaggeredGridLayoutManager -> 2
            is LinearLayoutManager -> orientation
            else -> -1
        }
    }

fun RecyclerView.vertical(
    spanCount: Int = 0,
    isStaggered: Boolean = false,
    closeScroll: Boolean = false,
    hasFixedSize: Boolean = false
): RecyclerView {
    this.setHasFixedSize(hasFixedSize)
    layoutManager = when (spanCount) {
        0, 1 -> object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return !closeScroll
            }
        }

        else -> {
            if (isStaggered) {
                object : StaggeredGridLayoutManager(spanCount, VERTICAL) {
                    override fun canScrollVertically(): Boolean {
                        return !closeScroll
                    }
                }
            } else {
                object : GridLayoutManager(context, spanCount) {
                    override fun canScrollVertically(): Boolean {
                        return !closeScroll
                    }
                }
            }
        }
    }
    return this
}

fun RecyclerView.horizontal(
    spanCount: Int = 0,
    isStaggered: Boolean = false,
    closeScroll: Boolean = false,
    hasFixedSize: Boolean = false
): RecyclerView {
    this.setHasFixedSize(hasFixedSize)
    layoutManager = when (spanCount) {
        0, 1 -> object : LinearLayoutManager(context, HORIZONTAL, false) {
            override fun canScrollHorizontally(): Boolean {
                return !closeScroll
            }
        }

        else -> {
            if (isStaggered) {
                object : StaggeredGridLayoutManager(spanCount, HORIZONTAL) {
                    override fun canScrollHorizontally(): Boolean {
                        return !closeScroll
                    }
                }
            } else {
                object : GridLayoutManager(context, spanCount, HORIZONTAL, false) {
                    override fun canScrollHorizontally(): Boolean {
                        return !closeScroll
                    }
                }
            }
        }
    }
    return this
}

/**
 * 开启下拉刷新，recyclerView必须被SmartRefreshLayout包裹
 */
fun RecyclerView.enablePullToRefresh(
    autoRefresh: Boolean = false, customRefreshLayout: (() -> RefreshHeader)? = null
): RecyclerView {
    var parent = parent
    while (parent != null && parent !is SmartRefreshLayout) {
        parent = parent.parent
    }
    if (parent is SmartRefreshLayout) {
        parent.setEnableRefresh(true)
        parent.setEnableFooterFollowWhenNoMoreData(true)
        if (autoRefresh) {
            val autoRefresh1 = parent.autoRefresh()
        }
        customRefreshLayout?.let {
            parent.setRefreshHeader(it.invoke())
        }
    } else {
        throw RuntimeException("recyclerView未被SmartRefreshLayout包裹，无法开启下拉刷新")
    }
    return this
}

/**
 * 根据需求自行配置下拉刷新框架
 */
fun RecyclerView.configSmartRefreshLayout(configSmartRefreshLayout: (SmartRefreshLayout) -> Unit): RecyclerView {
    var parent = parent
    while (parent != null && parent !is SmartRefreshLayout) {
        parent = parent.parent
    }
    if (parent is SmartRefreshLayout) {
        configSmartRefreshLayout.invoke(parent)
    } else {
        throw RuntimeException("recyclerView未被SmartRefreshLayout包裹，无法配置刷新布局")
    }
    return this
}

/**
 * 监听下拉刷新事件
 */
fun RecyclerView.listenerRefresh(refreshListener: (refreshLayout: RefreshLayout) -> Unit): RecyclerView {
    var parent = parent
    while (parent != null && parent !is SmartRefreshLayout) {
        parent = parent.parent
    }
    if (parent is SmartRefreshLayout) {
        parent.setOnRefreshListener {
            refreshListener.invoke(it)
        }
    } else {
        throw RuntimeException("recyclerView未被SmartRefreshLayout包裹，无法监听下拉刷新")
    }
    return this
}

/**
 * 监听上拉加载事件
 */
fun RecyclerView.listenerLoadMore(loadMoreListener: (refreshLayout: RefreshLayout, page: Int, lastId: String) -> Unit): RecyclerView {
    var parent = parent
    while (parent != null && parent !is SmartRefreshLayout) {
        parent = parent.parent
    }
    if (parent is SmartRefreshLayout) {
        parent.setOnLoadMoreListener {
            val adapter = adapter as? BaseRvAdapter<*, *>
            loadMoreListener.invoke(it, (adapter?.page ?: 1) + 1, adapter?.lastId ?: "")
        }
    } else {
        throw RuntimeException("recyclerView未被SmartRefreshLayout包裹，无法监听上拉加载")
    }
    return this
}

fun RecyclerView.resetPageNumber() {
    (adapter as? BaseRvAdapter<*, *>)?.page = 1
}

fun RecyclerView.disableRefresh(): RecyclerView {
    var parent = parent
    while (parent != null && parent !is SmartRefreshLayout) {
        parent = parent.parent
    }
    if (parent is SmartRefreshLayout) {
        parent.setEnableRefresh(false)
    } else {
        throw RuntimeException("recyclerView未被SmartRefreshLayout包裹，无法关闭下拉刷新")
    }
    return this
}

fun RecyclerView.disableLoadMore(): RecyclerView {
    var parent = parent
    while (parent != null && parent !is SmartRefreshLayout) {
        parent = parent.parent
    }
    if (parent is SmartRefreshLayout) {
        parent.setEnableLoadMore(false)
    } else {
        throw RuntimeException("recyclerView未被SmartRefreshLayout包裹，无法关闭上拉加载")
    }
    return this
}

/**
 * 关闭本次刷新/加载
 */
fun RecyclerView.finishRefreshOrLoadMore(
    success: Boolean = true,
    hasMore: Boolean = true,
    lastId: String = "",
): RecyclerView {
    var parent = parent
    while (parent != null && parent !is SmartRefreshLayout) {
        parent = parent.parent
    }
    (parent as? SmartRefreshLayout)?.let { smartRefreshLayout ->
        val rvAdapter = adapter as? BaseRvAdapter<*, *>
        if (smartRefreshLayout.isLoading) {
            if (success) {
                rvAdapter?.page = (rvAdapter?.page ?: 1) + 1
                rvAdapter?.lastId = lastId
            }
            if (hasMore) {
                smartRefreshLayout.finishLoadMore()
            } else {
                smartRefreshLayout.finishLoadMoreWithNoMoreData()
            }
        } else if (smartRefreshLayout.isRefreshing) {
            if (success) {
                rvAdapter?.page = 1
                rvAdapter?.lastId = lastId
            }
            if (hasMore) {
                smartRefreshLayout.finishRefresh()
            } else {
                smartRefreshLayout.finishRefreshWithNoMoreData()
            }
        } else {
            smartRefreshLayout.setNoMoreData(!hasMore)
        }
    }
    return this
}

/**
 * 开启上拉加载
 */
fun RecyclerView.enableLoadMore(customLoadMoreFooter: (() -> RefreshFooter)? = null): RecyclerView {
    var parent = parent
    while (parent != null && parent !is SmartRefreshLayout) {
        parent = parent.parent
    }
    if (parent is SmartRefreshLayout) {
        parent.setEnableLoadMore(true)
        parent.setEnableFooterFollowWhenNoMoreData(true)
        customLoadMoreFooter?.let {
            parent.setRefreshFooter(it.invoke())
        }
    } else {
        throw RuntimeException("recyclerView未被SmartRefreshLayout包裹，无法开启上拉加载")
    }
    return this
}

fun RecyclerView.registerEmptyLayout(
    targetView: View, emptyLayoutRes: Int, configEmptyViewHandler: ((View) -> Unit)? = null
): RecyclerView {
    targetView.post {
        (targetView.parent as? ViewGroup)?.let { parent ->
            val oldLayoutParams = targetView.layoutParams
            val index = parent.indexOfChild(targetView)
            parent.removeViewAt(index)
            val frameLayout = FrameLayout(context)
            frameLayout.addView(targetView, ViewGroup.LayoutParams(-1, -1))
            val emptyLayout = LayoutInflater.from(context).inflate(emptyLayoutRes, null, false)
            configEmptyViewHandler?.invoke(emptyLayout)
            emptyLayout.visibility = View.GONE
            emptyLayout.tag = "emptyLayout"
            frameLayout.id = hashCode()
            frameLayout.tag = "emptyLayoutParent"
            frameLayout.addView(emptyLayout, ViewGroup.LayoutParams(-1, -1))
            if (parent is SmartRefreshLayout) {
                parent.setRefreshContent(frameLayout, oldLayoutParams.width, oldLayoutParams.height)
            } else {
                parent.addView(frameLayout, index, oldLayoutParams)
            }
        } ?: kotlin.run {
            throw RuntimeException("父容器未知类型")
        }
    }
    return this
}

fun RecyclerView.showEmptyLayout(configEmptyViewHandler: ((View) -> Unit)? = null): RecyclerView {
    getCustomAdapter<Any>().refreshData(listOf())
    rootView.findViewById<ViewGroup>(hashCode())?.let { frameLayout ->
        frameLayout.getChildAt(0)?.visibility = View.GONE
        frameLayout.getChildAt(1)?.visibility = View.VISIBLE
        configEmptyViewHandler?.invoke(frameLayout.getChildAt(1))
    }
    return this
}

fun RecyclerView.hideEmptyLayout(): RecyclerView {
    try {
        val frameLayout = rootView.findViewById<ViewGroup>(hashCode())
        frameLayout?.getChildAt(0)?.visibility = View.VISIBLE
        frameLayout?.getChildAt(1)?.visibility = View.GONE
    } catch (e: Exception) {
    }
    return this
}

fun <T> RecyclerView.getCustomAdapter(): BaseRvAdapter<T, out RecyclerView.ViewHolder> {
    return (adapter as? BaseRvAdapter<T, *>)
        ?: kotlin.run { throw RuntimeException("RecyclerView未使用BaseRvAdapter") }
}

fun <T> RecyclerView.refreshData(
    datas: List<T>,
    append: Boolean = false,
    autoShowEmpty: Boolean = true,
    distinct: Boolean = false
): RecyclerView {
    if (datas.isNotEmpty()) {
        hideEmptyLayout()
    }
    (adapter as? BaseRvAdapter<T, *>)?.apply {
        if (append) {
            val tmpData = if (distinct) datas.distinct().filter { this.data?.contains(it) != true }
            else datas
            addData(tmpData)
        } else {
            refreshData(if (distinct) datas.distinct() else datas)
        }
    } ?: kotlin.run {
        throw RuntimeException("适配器非BaseRvDataBindingAdapter类型")
    }
    if ((adapter as? BaseRvAdapter<*, *>)?.data?.isEmpty() == true) {
        if (autoShowEmpty) {
            showEmptyLayout()
        }
    } else {
        hideEmptyLayout()
    }
    return this
}

fun <T> RecyclerView.insertData(datas: List<T>, index: Int): RecyclerView {
    hideEmptyLayout()
    (adapter as? BaseRvAdapter<T, *>)?.insertData(datas, index) ?: kotlin.run {
        throw RuntimeException("适配器非BaseRvDataBindingAdapter类型")
    }
    return this
}

fun <T> RecyclerView.removeData(data: T): RecyclerView {
    (adapter as? BaseRvAdapter<T, *>)?.removeData(data) ?: kotlin.run {
        throw RuntimeException("适配器非BaseRvDataBindingAdapter类型")
    }
    if ((adapter as? BaseRvAdapter<*, *>)?.data?.isEmpty() == true) {
        showEmptyLayout()
    } else {
        hideEmptyLayout()
    }
    return this
}

fun RecyclerView.refreshItem(position: Int): RecyclerView {
    adapter?.notifyItemChanged(position) ?: kotlin.run {
        throw RuntimeException("未配置适配器")
    }
    if ((adapter as? BaseRvAdapter<*, *>)?.data?.isEmpty() == true) {
        showEmptyLayout()
    } else {
        hideEmptyLayout()
    }
    return this
}

fun RecyclerView.refreshItem(data: Any): RecyclerView {
    (adapter as? BaseRvAdapter<*, *>)?.apply {
        this.data?.indexOf(data)?.let { notifyItemChanged(it) }
    } ?: kotlin.run {
        throw RuntimeException("适配器非BaseRvDataBindingAdapter类型")
    }
    if ((adapter as? BaseRvAdapter<*, *>)?.data?.isEmpty() == true) {
        showEmptyLayout()
    } else {
        hideEmptyLayout()
    }
    return this
}

fun <T> RecyclerView.itemClickListener(onClickListener: (view: View, data: T, position: Int) -> Unit): RecyclerView {
    (adapter as? BaseRvAdapter<T, *>)?.itemClickListener = onClickListener
    return this
}

fun <T> RecyclerView.itemLongClickListener(onLongClickListener: (view: View, data: T, position: Int) -> Boolean): RecyclerView {
    (adapter as? BaseRvAdapter<T, *>)?.itemLongClickListener = onLongClickListener
    return this
}

fun RecyclerView.divider(color: Int, size: Int): RecyclerView {
    return divider(color, size, size)
}

fun RecyclerView.divider(color: Int, horizontalSize: Int, verticalSize: Int): RecyclerView {
    val itemDecoration = when (layoutManager) {
        is GridLayoutManager -> {
            GridSpacingSupportRtlItemDecoration(
                (layoutManager as GridLayoutManager).spanCount, horizontalSize, verticalSize, false
            )
        }

        is StaggeredGridLayoutManager -> {
            GridSpacingSupportRtlItemDecoration(
                (layoutManager as StaggeredGridLayoutManager).spanCount,
                horizontalSize,
                verticalSize,
                false
            )
        }

        else -> RecyclerViewDivider(context, orientation).apply {
            setDrawable(GradientDrawable().apply {
                setColor(color)
                shape = GradientDrawable.RECTANGLE
                setSize(horizontalSize, verticalSize)
            })
        }
    }
    addItemDecoration(itemDecoration)
    return this
}

inline fun <reified T : Any, DBINDING : ViewDataBinding> RecyclerView.registerItemView(
    layoutRes: Int,
    noinline bindItemView: (binding: DBINDING, data: T, position: Int) -> Unit
): RecyclerView {
    (itemAnimator as? SimpleItemAnimator)?.apply {
        supportsChangeAnimations = false
        changeDuration = 0
    }
    if (adapter == null) {
        adapter = BaseRvMultipleDataBindingAdapter()
    }
    (adapter as? BaseRvMultipleDataBindingAdapter)?.registerItemLayout(
        context, layoutRes, T::class.java, bindItemView
    ) ?: kotlin.run { throw RuntimeException("使用的适配器不支持多样式布局") }
    return this
}

fun RecyclerView.page(): Int {
    val rvAdapter = adapter as? BaseRvAdapter<*, *>
    return rvAdapter?.page ?: 1
}