package cerbrendus.tasklist

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import cerbrendus.tasklist.ViewModels.ItemViewModel
import cerbrendus.tasklist.dataClasses.Group
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout
import org.jetbrains.anko.childrenSequence


// This activity holds the viewPager for the task lists for each group.
// The provided group is 'main' (groupId = -1), which holds all items for which no group has been set or for which the
// the group is set to be visible in main.
// The other groups are selected by their specified id.

fun Int.toPx() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()
const val ADMOB_ID = "ca-app-pub-1916462945338133~1136893406"

class MainActivity : AppCompatActivity(), OnRapidFloatingActionContentLabelListListener<MainActivity>,
    RewardedVideoAdListener {
    var rfabHelper : RapidFloatingActionHelper? = null
    private lateinit var rewardedVideoAd: RewardedVideoAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Ads stuff
        MobileAds.initialize(this, ADMOB_ID)
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        rewardedVideoAd.rewardedVideoAdListener = this

        //Get ViewModel
        val vm = ItemViewModel.create(this)

        //Setup Toolbar
        setSupportActionBar(findViewById(R.id.toolbar_main_activity))

        //Set the viewPager adapter
        if(findViewById<FrameLayout>(R.id.main_view_pager)!=null){
            val mainViewPagerAdapter = MainViewPagerAdapter(vm.groupList.value.orEmpty(),supportFragmentManager)
            val viewPager: ViewPager = findViewById<ViewPager>(R.id.main_view_pager)
            viewPager.adapter = mainViewPagerAdapter
            val tabLayout: TabLayout = findViewById(R.id.main_tab_layout)

            tabLayout.setupWithViewPager(viewPager)
            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            val tabStrip: LinearLayout = tabLayout.getChildAt(0) as LinearLayout
            vm.groupList.observe(this, Observer<List<Group>> {newGroupList->
                viewPager.adapter = MainViewPagerAdapter(newGroupList.orEmpty(),supportFragmentManager)

                for (i in 0 until tabStrip.childCount){
                    tabStrip.getChildAt(i).setOnLongClickListener {
                        if (i - POSITION_OFFSET >= 0) {
                            editGroupActivity(newGroupList.get(i - POSITION_OFFSET))
                            true
                        } else {
                            false
                        }
                    }
                }
            })
            Log.d("pager","adapter set")
        }

        //Get UI handles for RFAB
        val rfaLayout: RapidFloatingActionLayout = findViewById(R.id.activity_main_rfal)
        val rfaBtn: RapidFloatingActionButton = findViewById(R.id.rfab_add)

        //RapidFloatingActionButton setup
        val rfaContent = RapidFloatingActionContentLabelList(this)
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this)
        val items = mutableListOf<RFACLabelItem<Int>>(
            RFACLabelItem<Int>()
                .setLabel("new Task")
                .setResId(R.mipmap.ic_launcher_round)
                .setIconNormalColor(0xffd84315.toInt())
                .setWrapper(0),
            RFACLabelItem<Int>()
                .setLabel("new Workflow")
                .setResId(R.mipmap.ic_launcher_round)
                .setIconNormalColor(0xffd8a515.toInt())
                .setWrapper(1)
            )
        rfaContent.items = items.toList()
        rfaContent.setIconShadowColor(0xff888888.toInt())
            .setIconShadowDx(5.toPx())
            .setIconShadowDy(5.toPx())
        rfabHelper = RapidFloatingActionHelper(this,rfaLayout,rfaBtn,rfaContent).build()
    }

    private fun editGroupActivity(group: Group, type: Int = TYPE_VIEW) {
        val intent = Intent(this,CreateGroupActivity::class.java).apply{
            putExtra(TYPE_INTENT_KEY, type)
            putExtra(GROUP_KEY, group)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_actionbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.action_clear -> {
            clearCheckedItems()
            true
        }
        R.id.action_new_group -> {
            createNewGroup()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //Handle create new group button clicked
    private fun createNewGroup(){
        val vm = ItemViewModel.create(this)

        val intent = Intent(this,CreateGroupActivity::class.java)
        startActivity(intent)
    }

    //Handle clear button clicked
    private fun clearCheckedItems() {
        val vm = ItemViewModel.create(this)
        vm.recentClearedItems = vm.allCheckedItems.value.orEmpty()
        vm.clearCheckedItems()
        //make snackbar with undo button when recentClearedItems.isNotEmpty()
        //if button is clicked, then vm.undoClear()
        if (vm.recentClearedItems.isNotEmpty()){
            val undoSnackbar = Snackbar.make(findViewById<CoordinatorLayout>(R.id.main_top_layout),"${vm.recentClearedItems.size} item${if(vm.recentClearedItems.size!=1) "s" else ""} cleared",Snackbar.LENGTH_LONG)
            undoSnackbar.setAction("UNDO") {vm.undoClear()}
            undoSnackbar.show()
        }
    }

    //Handle RFAC icon clicked
    override fun onRFACItemIconClick(position: Int, item: RFACLabelItem<MainActivity>?) {
        rfabHelper!!.toggleContent()
        when(position){
            0 -> openEditTaskActivity(TYPE_ADD)
        }
    }

    //Handle clicked label of RFAC
    override fun onRFACItemLabelClick(position: Int, item: RFACLabelItem<MainActivity>?) {
        rfabHelper!!.toggleContent()
        when(position){
            0 -> openEditTaskActivity(TYPE_ADD)
        }
    }

    //Open an instance of EditTaskActivity
    private fun openEditTaskActivity(type: Int) {
        val vm = ItemViewModel.create(this)

        val intent = Intent(this,EditTaskActivity::class.java).apply {
            putExtra(TYPE_INTENT_KEY,type)
        }
        //vm.editType.value = type
        startActivity(intent)
    }


    override fun onRewardedVideoAdClosed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates
    }

    override fun onRewardedVideoAdLeftApplication() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRewardedVideoAdLoaded() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRewardedVideoAdOpened() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRewardedVideoCompleted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRewarded(p0: RewardItem?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRewardedVideoStarted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
