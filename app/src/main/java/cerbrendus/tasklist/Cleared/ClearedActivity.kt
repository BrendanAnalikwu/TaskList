package cerbrendus.tasklist.Cleared

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.R
import kotlinx.coroutines.launch

class ClearedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cleared)
        val vm = ClearedViewModel.create(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar_cleared_activity)
        setSupportActionBar(toolbar)

        //Get RecyclerView handle
        val recyclerView: RecyclerView = findViewById(R.id.cleared_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val itemDecor = DividerItemDecoration(this, (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(itemDecor)

        //Set RecyclerView adapter
        val adapter = ClearedItemAdapter(vm.allClearedItems.value.orEmpty(), vm.groupColorMap.value.orEmpty(), this)
        recyclerView.adapter = adapter
        vm.allClearedItems.observe(this, Observer { adapter.setItems(it) })
        vm.groupColorMap.observe(this, Observer { adapter.setColorMap(it) })

        // Set drag handler
        val dragHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean =
                false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                adapter.removeItem(vm.allClearedItems.value.orEmpty().toMutableList().apply { removeAt(pos) }, pos)
                vm.scope.launch {
                    vm.unclearItem(pos)
                }
            }
        }
        val dragHelper = ItemTouchHelper(dragHandler)
        dragHelper.attachToRecyclerView(recyclerView)
    }
}
