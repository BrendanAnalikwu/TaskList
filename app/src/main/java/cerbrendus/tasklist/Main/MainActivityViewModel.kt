package cerbrendus.tasklist.Main

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import cerbrendus.tasklist.BaseClasses.TYPE_ADD
import cerbrendus.tasklist.Database.ItemRepository
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

    private fun undoClear() {
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

    init { editType.value = TYPE_ADD
    }

    fun clearCheckedItems(showUndoSnackbar : (Int, ()->Unit) -> Unit) {
        recentClearedItems = allCheckedItems.value.orEmpty()
        for (item in recentClearedItems){
            item.cleared = true
        }
        update(*recentClearedItems.toTypedArray())

        //make snackbar with undo button when recentClearedItems.isNotEmpty()
        //if button is clicked, then vm.undoClear()
        showUndoSnackbar(recentClearedItems.size,::undoClear)
    }

    fun tabPosToGroupId(pos: Int) : Long =
        if(pos - POSITION_OFFSET < 0) (pos - POSITION_OFFSET).toLong()
        else groupList.value!![pos - POSITION_OFFSET].id!!

    companion object {
        private var vm: MainActivityViewModel? = null
        fun create(activity: FragmentActivity): MainActivityViewModel =
            if(vm ===null) ViewModelProviders.of(activity).get(
                MainActivityViewModel::class.java)
            else vm!!
    }
}