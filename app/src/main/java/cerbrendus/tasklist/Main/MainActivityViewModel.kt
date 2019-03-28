package cerbrendus.tasklist.Main

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import cerbrendus.tasklist.Database.ItemRepository
import cerbrendus.tasklist.EditTaskItem.TYPE_ADD
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem

//Created by Brendan on 30-12-2018.
class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = ItemRepository.create(application)
    val allItems = itemRepo.getAll()
    val allClearedItems = itemRepo.getAllCleared()
    val allCheckedItems = itemRepo.getAllChecked()
    var recentClearedItems : List<TaskItem> = emptyList()

    fun insert(vararg item: TaskItem) {itemRepo.insert(*item)}
    fun update(vararg item: TaskItem) {itemRepo.update(*item)}
    fun delete(vararg item: TaskItem) {itemRepo.delete(*item)}


    fun clearCheckedItems() {
        for (item in allCheckedItems.value.orEmpty()){
            item.cleared = true
            //item.checked = false //Causer of ALOTTA trouble or not
        }
        update(*allCheckedItems.value.orEmpty().toTypedArray())
    }

    fun undoClear() {
        for (item in recentClearedItems){
            item.cleared = false
            item.checked = true
        }
        update(*recentClearedItems.toTypedArray())
    }

    val groupList = itemRepo.getGroupList()
    fun createGroup(group: Group) = itemRepo.createGroup(group)
    fun getAllItemsInGroup(groupId: Long) = itemRepo.getAllItemsInGroup(groupId)

    var editType : MutableLiveData<Int> = MutableLiveData()
    var ETAOpenedAsView = false

    init { editType.value = TYPE_ADD
    }

    companion object {
        private var vm: MainActivityViewModel? = null
        fun create(activity: FragmentActivity): MainActivityViewModel =
            if(vm ===null) ViewModelProviders.of(activity).get(
                MainActivityViewModel::class.java)
            else vm!!
    }
}