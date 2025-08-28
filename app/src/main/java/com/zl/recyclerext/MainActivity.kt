package com.zl.recyclerext

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.zl.recyclerext.databinding.ItemDataBinding
import com.zl.recyclerviewext.divider
import com.zl.recyclerviewext.horizontal
import com.zl.recyclerviewext.refreshData
import com.zl.recyclerviewext.registerItemView
import com.zl.recyclerviewext.vertical

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<RecyclerView>(R.id.recycler_view)?.apply {
            vertical(5) { position ->
                if (position == 14) {
                    5
                } else 1
            }
            divider(Color.RED, 30, 10, includeEdge = false)
            registerItemView<TestBean, ItemDataBinding>(R.layout.item_data) { binding, data, position ->
                binding.text.text = (0..100000).random().toString()
            }
            refreshData(
                listOf(
                    TestBean(),
                    TestBean(),
                    Test1Bean(),
                    TestBean(),
                    TestBean(),
                    TestBean(),
                    TestBean(),
                    TestBean(),
                    TestBean(),
                    TestBean(),
                    TestBean(),
                    TestBean(),
                    TestBean(),
                    TestBean(),
                    TestBean(),
                ), distinct = true
            )
        }
    }
}