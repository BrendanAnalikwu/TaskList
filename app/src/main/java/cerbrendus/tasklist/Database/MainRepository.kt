package cerbrendus.tasklist.Database

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.anko.doAsync

//Created by Brendan on 30-12-2018.
class ItemRepository(application: Application) {
    private val itemDB = ItemDatabase.getInstance(application)!!
    private val itemDAO = itemDB.itemDAO()
    private val scope = CoroutineScope(Dispatchers.Default)
    private val allItems: LiveData<List<TaskItem>> = Transformations.map(itemDAO.getAllItems()) {unordered ->
        unordered.sortedBy { it.priority }
    }
    private val allClearedItems: LiveData<List<TaskItem>> = Transformations.map(itemDAO.getAllClearedItems()){unordered ->
        unordered.sortedBy { it.priority }
    }
    private val allCheckedItems: LiveData<List<TaskItem>> = Transformations.map(itemDAO.getAllCheckedItems()){unordered ->
        unordered.sortedBy { it.priority }
    }
    private val items: LiveData<List<TaskItem>> = Transformations.map(itemDAO.getItems()){unordered ->
        unordered.sortedBy { it.priority }
    }
    private val groupList: LiveData<List<Group>> = itemDAO.getGroupList()

    fun getAll() = allItems
    fun getAllCleared() = allClearedItems
    fun getAllChecked() = allCheckedItems

    fun getGroupList() = groupList
    fun getAllItemsInGroup(groupId : Long) : LiveData<List<TaskItem>> = Transformations.map(allItems) {all ->
        all.filter{i -> i.group_id==groupId}
    }
    fun getGroupFromId(_id : Long) : Group? = groupList.value?.firstOrNull { it.id == _id }

    /**
     * Gets taskItem object from id in the list of [all items][items]
     * @param id the id from the item to be fetched
     * @return the [TaskItem] object to be fetched or null
     */
    fun getItemFromId(id : Long) = items.value?.firstOrNull{it.id!! == id}
    fun getItems() = items
    private val groupTitlesList : LiveData<List<String>> = Transformations.map(groupList) { groupList ->
        groupList.map{ group -> group.title ?: "" }
    }
    fun getGroupTitlesList() : LiveData<List<String>> = groupTitlesList

    fun updateChecked(id: Long, checked_val : Boolean) {
        doAsync {
            itemDAO.updateChecked(id, checked_val)
        }
    }

    /*fun updatePriority(vararg pair: Pair<Long,Long>) : Boolean {
        val list = mutableListOf<TaskItem>()
        Log.i("tasklist.debug","${allItems.value.orEmpty().map{it.id}}")
        for (p in pair) {
            val i = allItems.value.orEmpty().find { it.id == p.first }?.apply { priority = p.second }
            if (i != null) list.add(i)
            Log.i("tasklist.debug","${p.first}")
        }
        if(list.isNotEmpty()) doAsync { itemDAO.updateItems(*list.toTypedArray())}
        else {
            Log.i("tasklist.debug",list.toString());return false}
        return true
    }*/


    suspend fun insertForResult(item: TaskItem) : Long {
        val p =  itemDAO.getMaxPriority() + 1
        return itemDAO.insertForResult(item.apply { priority =  p})
    }

    fun update(vararg item: TaskItem) {
        doAsync {
            itemDAO.updateItems(*item)
        }
    }

    suspend fun delete(vararg item: TaskItem) {
        itemDAO.deleteItems(*item)
    }

    fun createGroup(group: Group) {
        doAsync {
            itemDAO.createGroup(group)
        }
    }

    fun deleteGroup(group: Group) {
        doAsync {
            itemDAO.deleteGroup(group)
        }
    }

    fun updateGroup(group: Group) {
        doAsync {
            itemDAO.updateGroup(group)
        }
    }

    companion object {
        private var itemRepo : ItemRepository? = null
        fun create(application : Application) : ItemRepository {
            return itemRepo
                ?: ItemRepository(application)
        }
    }
}