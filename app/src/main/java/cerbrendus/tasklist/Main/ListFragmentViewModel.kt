package cerbrendus.tasklist.Main

import android.app.Application
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import cerbrendus.tasklist.dataClasses.TaskItem

class ListFragmentViewModel(application: Application) : AndroidViewModel(application){
    lateinit var aVM : MainActivityViewModel
    var groupId: Long = -1
    lateinit var itemList: LiveData<List<TaskItem>>
    var oldList: List<TaskItem> = emptyList()
    var movedItemList: MutableList<TaskItem> = mutableListOf()
    var movedAllItemList: MutableList<TaskItem> = mutableListOf()
    lateinit var fragment: ListFragment

    fun configure(fragment: Fragment) {
        if (fragment.activity == null) throw IllegalStateException("Fragment needs to be attached")
        else aVM = MainActivityViewModel.create(fragment.activity!!)
        this.fragment = fragment as ListFragment

    }

    fun configure(_groupId: Long) {
        groupId = _groupId
        itemList = when (groupId) {// List with items to be shown in this fragment
            (-1).toLong() -> aVM.allItems
            else -> aVM.getAllItemsInGroup(groupId)
        }
        oldList = itemList.value.orEmpty()
        movedItemList = itemList.value.orEmpty().toMutableList() //List of the items in this fragment in the order as shown
        movedAllItemList = aVM.allItems.value.orEmpty().toMutableList()

    }

    fun itemListChange(newList: List<TaskItem>, adapter: ItemAdapter?){
        //called (from the itemList observer) to handle the change in the itemList
        //TODO: implement ranges that changed
        when {
            oldList.size > newList.size -> {//deletion
                val newId = newList.map{it.id}
                val oldId = oldList.map{it.id}
                var suc = false

                for (pos in newId.indices) if(newId[pos] != oldId[pos]) { adapter?.onItemDelete(newList,pos); suc = true}
                if(!suc) { adapter?.onItemDelete(newList,oldList.lastIndex) } //the last item was deleted
            }

            oldList.size < newList.size -> {//insertion
                adapter?.onItemInserted(newList,newList.lastIndex)
            }

            oldList.map{it.id} == newList.map{it.id} -> {//internal change (allready handles ranges)
                val changedIndices = newList.zip(oldList).filter { it.first != it.second }.map{newList.indexOf(it.first)}
                if(changedIndices.isNotEmpty()) adapter?.onItemChanged(newList,changedIndices.first(),changedIndices.size)
            }
            oldList.sortedBy { it.id } == newList.sortedBy { it.id } -> {//item moved
                //nothing really needs to be done here, everything is handle in other methods
                if(!fragment.userVisibleHint) {adapter?.onDatasetChanged(newList);Log.d("visbl","Fragment ${groupId} is not visible")}
            }
            else -> {//anything else
                adapter?.onDatasetChanged(newList)
            }

        }

        oldList = newList
        movedItemList = newList.toMutableList()
    }

    fun onItemMoved(adapter: ItemAdapter?, from: Int, to: Int) {
        val list = movedItemList.toMutableList() //copy the list, for work in this method
        val allList = movedAllItemList.toMutableList() //copy the list of all items

        val allFrom = movedAllItemList.indexOf(movedItemList[from]) //get the index in the list of all (moved) items
        val allTo = movedAllItemList.indexOf(movedItemList[to])

        when {
            from < to -> {
                for (i in from..(to-1)){ list[i] = movedItemList[i+1] }
                list[to] = movedItemList[from]
                for (j in allFrom..(allTo-1)){ allList[j] = movedAllItemList[j+1] }
                allList[allTo] = movedAllItemList[allFrom]
            }
            to < from -> {
                for(i in (to+1)..from) { list[i] = movedItemList[i-1] }
                list[to] = movedItemList[from]
                for(j in (allTo+1)..allFrom) { allList[j] = movedAllItemList[j-1] }
                allList[allTo] = movedAllItemList[allFrom]
            }
            else -> return
        }

        movedAllItemList = allList
        movedItemList = list
        adapter?.onItemMoved(movedItemList,from, to)
        savePriority()
    }

    fun savePriority() {
        val priorities = aVM.allItems.value.orEmpty().map{ it.priority }
        movedAllItemList.map{it.priority}
        val allItemPriority = movedAllItemList.zip(priorities)
        val changedItemPriority = allItemPriority.filter { it.first.priority != it.second }
        val updateList = changedItemPriority.map{ it.first.priority = it.second; it.first } //set priorities to what they should be
        aVM.update(*updateList.toTypedArray())
    }



    companion object {
        private var vm: ListFragmentViewModel? = null
        fun create(fragment: Fragment): ListFragmentViewModel = vm
            ?: ViewModelProviders.of(fragment).get(ListFragmentViewModel::class.java).apply{configure(fragment)}
    }
}