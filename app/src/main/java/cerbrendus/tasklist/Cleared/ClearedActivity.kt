package cerbrendus.tasklist.Cleared

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.Main.MainActivity
import cerbrendus.tasklist.R
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import org.jetbrains.anko.itemsSequence

class ClearedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cleared)
        val vm = ClearedViewModel.create(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar_cleared_activity)
        setSupportActionBar(toolbar)

        //Set the drawer
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_drawer_item -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.groups_drawer_item -> {
                }
                R.id.cleared_drawer_item -> {}
                R.id.settings_drawer_item -> {
                }
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }

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

    override fun onResume() {
        super.onResume()
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.menu.itemsSequence().forEach { it.isChecked = it.itemId == R.id.cleared_drawer_item }
    }
}
