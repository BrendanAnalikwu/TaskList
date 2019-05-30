package cerbrendus.tasklist.Main

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
import cerbrendus.tasklist.BaseClasses.GROUPLIST_KEY
import cerbrendus.tasklist.BaseClasses.TYPE_ADD
import cerbrendus.tasklist.BaseClasses.TYPE_INTENT_KEY
import cerbrendus.tasklist.BaseClasses.TYPE_VIEW
import cerbrendus.tasklist.EditGroup.CreateGroupActivity
import cerbrendus.tasklist.EditGroup.GROUP_KEY
import cerbrendus.tasklist.EditTaskItem.CURRENT_GROUP_ID_KEY
import cerbrendus.tasklist.EditTaskItem.EditTaskActivity
import cerbrendus.tasklist.EditTaskItem.ITEM_LIST_KEY
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener


// This activity holds the viewPager for the task lists for each group.
// The provided group is 'main' (groupId = -1), which holds all items for which no group has been set or for which the
// the group is set to be visible in main.
// The other groups are selected by their specified id.

fun Int.toPx() : Int = (this * Resources.getSystem().displayMetrics.density).toInt()
const val ADMOB_ID = "ca-app-pub-1916462945338133~1136893406"
const val ADMOB_TEST_ID = "ca-app-pub-3940256099942544/5224354917"

class MainActivity : AppCompatActivity(), OnRapidFloatingActionContentLabelListListener<MainActivity>,
    RewardedVideoAdListener {
    var rfabHelper : RapidFloatingActionHelper? = null
    private lateinit var rewardedVideoAd: RewardedVideoAd
    private lateinit var vm : MainActivityViewModel
    private lateinit var  tabLayout : TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Ads stuff
        MobileAds.initialize(this, ADMOB_ID)
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        rewardedVideoAd.rewardedVideoAdListener = this

        //Get ViewModel
        vm = MainActivityViewModel.create(this)

        //Setup Toolbar
        setSupportActionBar(findViewById(R.id.toolbar_main_activity))

        //Set the viewPager adapter
        if(findViewById<FrameLayout>(R.id.main_view_pager)!=null){
            val mainViewPagerAdapter = MainViewPagerAdapter(
                vm.groupList.value.orEmpty(),
                supportFragmentManager,
                this
            )
            val viewPager: ViewPager = findViewById<ViewPager>(R.id.main_view_pager)
            viewPager.adapter = mainViewPagerAdapter
            tabLayout = findViewById(R.id.main_tab_layout)

            tabLayout.setupWithViewPager(viewPager)
            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            val tabStrip: LinearLayout = tabLayout.getChildAt(0) as LinearLayout
            vm.groupList.observe(this, Observer<List<Group>> {newGroupList->
                viewPager.adapter = MainViewPagerAdapter(
                    newGroupList.orEmpty(),
                    supportFragmentManager,
                    this
                )

                for (i in 0 until tabStrip.childCount){
                    tabStrip.getChildAt(i).setOnLongClickListener {
                        if (i - POSITION_OFFSET >= 0) {
                            editGroupActivity(newGroupList.get(i - POSITION_OFFSET),vm.allItems.value.orEmpty())
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
                .setLabel(getString(R.string.label_new_task))
                .setResId(R.mipmap.ic_launcher_round)
                .setIconNormalColor(0xffd84315.toInt())
                .setWrapper(0),
            RFACLabelItem<Int>()
                .setLabel(getString(R.string.label_new_sublist))
                .setResId(R.mipmap.ic_launcher_round)
                .setIconNormalColor(0xffd8a515.toInt())
                .setWrapper(1)
            )
        rfaContent.items = items.toList()
        rfaContent.setIconShadowColor(0xff888888.toInt())
            .setIconShadowDx(5.toPx())
            .setIconShadowDy(5.toPx())
        rfabHelper = RapidFloatingActionHelper(this,rfaLayout,rfaBtn,rfaContent).build()


        loadRewardedVideo()
    }

    private fun editGroupActivity(group: Group, itemList : List<TaskItem>, type: Int = TYPE_VIEW) {
        val intent = Intent(this, CreateGroupActivity::class.java).apply{
            putExtra(TYPE_INTENT_KEY, type)
            putExtra(GROUP_KEY, group)
            putExtra(ITEM_LIST_KEY, ArrayList(itemList))
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_actionbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.action_clear -> {
            vm.clearCheckedItems { numCleared, undoClear ->
                if(numCleared > 0) {
                    val undoSnackbar = Snackbar.make(findViewById<CoordinatorLayout>(R.id.main_top_layout),
                        "${numCleared} item${if(numCleared!=1) "s" else ""} cleared",
                        Snackbar.LENGTH_LONG)
                    undoSnackbar.setAction("UNDO") { undoClear()}
                    undoSnackbar.show()
                }
            }
            true
        }
        R.id.action_new_group -> {
            if (rewardedVideoAd.isLoaded) {
                rewardedVideoAd.show()
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //Handle create new group button clicked
    private fun createNewGroup(){
        val intent = Intent(this, CreateGroupActivity::class.java)
        startActivity(intent)
    }

    //Handle RFAC icon clicked
    override fun onRFACItemIconClick(position: Int, item: RFACLabelItem<MainActivity>?) {
        rfabHelper!!.toggleContent()

        when(position){
            0 -> openEditTaskActivity(TYPE_ADD, vm.tabPosToGroupId(tabLayout.selectedTabPosition))
            1 -> openEditSublistActivity(TYPE_ADD, vm.tabPosToGroupId(tabLayout.selectedTabPosition))
        }
    }

    //Handle clicked label of RFAC
    override fun onRFACItemLabelClick(position: Int, item: RFACLabelItem<MainActivity>?) {
        rfabHelper!!.toggleContent()
        when(position){
            0 -> openEditTaskActivity(TYPE_ADD, vm.tabPosToGroupId(tabLayout.selectedTabPosition))
            1 -> openEditSublistActivity(TYPE_ADD, vm.tabPosToGroupId(tabLayout.selectedTabPosition))
        }
    }

    //Open an instance of EditTaskActivity
    private fun openEditTaskActivity(type: Int, group_id: Long) {
        val intent = Intent(this, EditTaskActivity::class.java)
            .putExtra(TYPE_INTENT_KEY,type)
            .putExtra(CURRENT_GROUP_ID_KEY,group_id)
            .putParcelableArrayListExtra(GROUPLIST_KEY,ArrayList(vm.groupList.value!!))
        startActivity(intent)
    }

    //Open an instance of EditSublistActivity
    private fun openEditSublistActivity(type: Int, group_id: Long) {
        /*val intent = Intent(this, EditSublistActivity::class.java)
            .putExtra(TYPE_INTENT_KEY,type)
            .putExtra(CURRENT_GROUP_ID_KEY,group_id)
            .putParcelableArrayListExtra(GROUPLIST_KEY,ArrayList(vm.groupList.value!!))
        startActivity(intent)*/
    }

    // Load rewarded video
    private fun loadRewardedVideo(){
        rewardedVideoAd.loadAd(ADMOB_TEST_ID, AdRequest.Builder().build()) //TODO: Replace test id before publishing
    }

    // Event methods for rewarded ads
    override fun onRewarded(reward: RewardItem?) {
        //Toast.makeText(this, "onRewarded!",Toast.LENGTH_SHORT).show()
        createNewGroup()
    }

    override fun onRewardedVideoAdClosed() {
        loadRewardedVideo()
    }

    override fun onRewardedVideoAdLeftApplication() {}

    override fun onRewardedVideoAdLoaded() {}

    override fun onRewardedVideoAdOpened() {}

    override fun onRewardedVideoCompleted() {}

    override fun onRewardedVideoStarted() {}

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {}

    // Forwarding the activity's lifecycle events to the rewarded ad
    override fun onPause() {
        super.onPause()
        rewardedVideoAd.pause(this)
    }

    override fun onResume() {
        super.onResume()
        rewardedVideoAd.resume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        rewardedVideoAd.destroy(this)
    }

    //TODO: implement new rewarded video API when out of beta
    //TODO: implement ad event functions

}
