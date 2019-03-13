package cerbrendus.tasklist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem
import org.jetbrains.anko.doAsync

//Created by Brendan on 30-12-2018.
class ItemRepository(application: Application) {
    private val itemDB = ItemDatabase.getInstance(application)!!
    private val itemDAO = itemDB.itemDAO()
    private val allItems: LiveData<List<TaskItem>> = itemDAO.getAllItems()
    private val allClearedItems: LiveData<List<TaskItem>> = itemDAO.getAllClearedItems()
    private val allCheckedItems: LiveData<List<TaskItem>> = itemDAO.getAllCheckedItems()
    fun getAll() = allItems
    fun getAllCleared() = allClearedItems
    fun getAllChecked() = allCheckedItems

    fun getGroupFromId(id : Int) = groupList.value?.firstOrNull{it.id == id.toLong()}
    private val groupList: LiveData<List<Group>> = itemDAO.getGroupList()
    fun getAllItemsInGroup(groupId : Int) : LiveData<List<TaskItem>> = Transformations.map(allItems) {all ->
        all.filter{i -> i.group_id==groupId}
    }
    fun getGroupList() = groupList
    private val groupTitlesList : LiveData<List<String>> = Transformations.map(groupList) { groupList ->
        val titles : MutableList<String> = mutableListOf()
        for(group in groupList){
            titles.add(group.title)
        }
        titles.toList()
    }
    fun getGroupTitlesList() : LiveData<List<String>> = groupTitlesList

    fun insert(vararg item: TaskItem) {
        doAsync {
            itemDAO.insertItems(*item)
        }
    }

    fun update(vararg item: TaskItem) {
        doAsync {
            itemDAO.updateItems(*item)
        }
    }

    fun delete(vararg item: TaskItem) {
        doAsync {
            itemDAO.deleteItems(*item)
        }
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
}