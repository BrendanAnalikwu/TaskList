package cerbrendus.tasklist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import cerbrendus.tasklist.dataClasses.Group

//Amount of pages to be created before the first user made group. Should be at least 1 to include a main item list
const val POSITION_OFFSET = 1

//Adapter for the viewPager with fragments for the group item lists.
//The groupId is passed to the fragment.
class MainViewPagerAdapter(_groupList: List<Group>,fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private var groupList = _groupList

    //This method creates new instances of the fragments
    override fun getItem(position: Int): Fragment {
        Log.d("pager","getItem($position)")
        val groupId: Long = groupIdFromPosition(position)
        val fragment = ListFragment.newInstance(groupId)
        return fragment
    }

    private fun groupIdFromPosition(position: Int): Long {
        val groupListIndex: Int = position - POSITION_OFFSET
        val groupId: Long = if (groupListIndex < 0) groupListIndex.toLong() else groupList[groupListIndex].id!!
        return groupId
    }

    override fun getPageTitle(position: Int): CharSequence? = when(position - POSITION_OFFSET) {
        -1 -> "Main"
        else -> groupList.get(position - POSITION_OFFSET).title
    }

    override fun getCount(): Int = groupList.size + POSITION_OFFSET;

    fun setPages(_groupList: List<Group>){
        groupList = _groupList
        notifyDataSetChanged()
    }

}