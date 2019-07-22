package cerbrendus.tasklist.Main

import android.app.Application
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import cerbrendus.tasklist.Database.ItemRepository
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListFragmentViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var aVM: MainActivityViewModel
    var groupId: Long = -1
    lateinit var itemList: LiveData<List<TaskItem>>
    private var oldList: List<TaskItem> = emptyList()
    private var movedItemList: MutableList<TaskItem> = mutableListOf()
    var movedAllItemList: MutableList<TaskItem> = mutableListOf()
    lateinit var fragment: ListFragment
    private lateinit var itemRepo: ItemRepository
    fun getGroup(): Group? = aVM.groupList.value?.firstOrNull { groupId == it.id }

    fun configure(fragment: Fragment) {
        if (fragment.activity == null) throw IllegalStateException("Fragment needs to be attached")
        else aVM = MainActivityViewModel.create(fragment.activity!!)
        this.fragment = fragment as ListFragment
        itemRepo = ItemRepository.create(fragment.activity!!.application)
    }

    fun configure(_groupId: Long) {
        groupId = _groupId
        itemList = when (groupId) {// List with items to be shown in this fragment
            (-1).toLong() -> aVM.allItems
            else -> aVM.getAllItemsInGroup(groupId)
        }
        oldList = itemList.value.orEmpty()
        movedItemList =
            itemList.value.orEmpty().toMutableList() //List of the items in this fragment in the order as shown
        movedAllItemList = aVM.allItems.value.orEmpty().toMutableList()
    }

    suspend fun updateChecked(id: Long, checked_val: Boolean) {
        itemRepo.updateChecked(id, checked_val)
    }

    fun itemListChange(newList: List<TaskItem>, adapter: ItemAdapter?) {
        //called (from the itemList observer) to handle the change in the itemList
        when {
            oldList.size > newList.size -> {//deletion
                val newId = newList.map { it.id }
                val oldId = oldList.map { it.id }
                var suc = false

                for (pos in newId.indices) if (newId[pos] != oldId[pos]) {
                    adapter?.onItemDelete(newList, pos); suc = true
                }
                if (!suc) {
                    adapter?.onItemDelete(newList, oldList.lastIndex)
                } //the last item was deleted
            }

            oldList.size < newList.size -> {//insertion
                adapter?.onItemInserted(newList, newList.lastIndex)
            }

            /*oldList.asSequence().map { it.id }.sortedBy { it }.toList() == newList.asSequence().map { it.id }.sortedBy { it }.toList() && oldList.map { it.id } != newList.map { it.id } -> {//item moved
                //nothing really needs to be done here, everything is handle in other methods
                if (fragment.userVisibleHint) {
                    //adapter is already notified
                    Log.i("tasklist.debug", "Noticed movement")
                    adapter?.setData(newList)
                } else {
                    adapter?.onDatasetChanged(newList)
                    Log.d("tasklist.debug", "Fragment $groupId is not visible, so used DatasetChanged")
                }
            }

            oldList.map { it.id } == newList.map { it.id } && oldList != newList -> {//internal change (already handles ranges)
                val changedIndices =
                    newList.zip(oldList).asSequence().filter { it.first != it.second }.map { newList.indexOf(it.first) }
                        .toList()
                adapter?.onDatasetChanged(newList)
            }
            oldList == newList -> {
                adapter?.onDatasetChanged(newList)
            }*/
            else -> {//anything else
                adapter?.onDatasetChanged(newList)
            }

        }
        Log.i("tasklist.debug.tok", "Brendan")
        oldList = newList.toMutableList()
        movedItemList = newList.toMutableList()
        Log.i("tasklist.debug.b", newList.map { it.priority }.toString())
    }

    fun onItemMoved(adapter: ItemAdapter?, from: Int, to: Int) {
        val list = movedItemList.toMutableList() //copy the list, for work in this method
        val allList = movedAllItemList.toMutableList() //copy the list of all items

        val allFrom = movedAllItemList.indexOf(movedItemList[from]) //get the index in the list of all (moved) items
        val allTo = movedAllItemList.indexOf(movedItemList[to])

        when {
            from < to -> {
                for (i in from until to) {
                    list[i] = movedItemList[i + 1]
                }
                list[to] = movedItemList[from]
                for (j in allFrom until allTo) {
                    allList[j] = movedAllItemList[j + 1]
                }
                allList[allTo] = movedAllItemList[allFrom]
            }
            to < from -> {
                for (i in (to + 1)..from) {
                    list[i] = movedItemList[i - 1]
                }
                list[to] = movedItemList[from]
                for (j in (allTo + 1)..allFrom) {
                    allList[j] = movedAllItemList[j - 1]
                }
                allList[allTo] = movedAllItemList[allFrom]
            }
            else -> return
        }

        movedAllItemList = allList.toMutableList()
        val priorities = aVM.allItems.value.orEmpty().asSequence().map { it.priority }.sorted().toList()
        movedItemList = list.toMutableList()
        adapter?.onItemMoved(movedItemList, from, to)
    }

    fun savePriority() {
        val priorities = aVM.allItems.value.orEmpty().map { it.priority }
        Log.i("tasklist.debug.a", movedAllItemList.map { it.priority }.toString())

        val allItemPriority = movedAllItemList.zip(priorities)
        val changedItemPriority = allItemPriority.filter { it.first.priority != it.second }
        val updateList =
            changedItemPriority.map { it.first.apply { priority = it.second } } //set priorities to what they should be
        CoroutineScope(Dispatchers.Default).launch { aVM.update(*updateList.toTypedArray()) }
        Log.i("tasklist.debug.tik", "Brendan")
    }


    companion object {
        private var vm: ListFragmentViewModel? = null
        fun create(fragment: Fragment): ListFragmentViewModel = vm
            ?: ViewModelProviders.of(fragment).get(ListFragmentViewModel::class.java).apply { configure(fragment) }
    }
}