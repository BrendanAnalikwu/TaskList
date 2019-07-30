package cerbrendus.tasklist.Cleared

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.R

class ClearedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cleared)
        val vm = ClearedViewModel.create(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar_cleared_activity)
        setSupportActionBar(toolbar)

        //Get RecyclerView handle
        val recyclerView : RecyclerView = findViewById(R.id.cleared_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val itemDecor = DividerItemDecoration(this, (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(itemDecor)

        //Set RecyclerView adapter
        val adapter = ClearedItemAdapter(vm.allClearedItems.value.orEmpty(), this)
        recyclerView.adapter = adapter
        vm.allClearedItems.observe(this, Observer {adapter.setItems(it)})
    }
}
