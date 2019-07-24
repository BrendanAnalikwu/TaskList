package cerbrendus.tasklist.Main

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import cerbrendus.tasklist.BaseClasses.TYPE_ADD
import cerbrendus.tasklist.Database.ItemRepository
import cerbrendus.tasklist.dataClasses.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//Created by Brendan on 30-12-2018.
class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = ItemRepository.create(application)
    val allItems = itemRepo.getAllVisibleUnclearedItems()
    val allClearedItems = itemRepo.getAllCleared()
    val allCheckedItems = itemRepo.getAllChecked()
    private var recentClearedItems: List<TaskItem> = emptyList()
    private val scope = CoroutineScope(Dispatchers.Default)

    suspend fun update(vararg item: TaskItem) {
        itemRepo.update(*item)
    }

    private fun undoClear() {
        for (item in recentClearedItems) {
            item.cleared = false
            item.checked = true
        }
        scope.launch { update(*recentClearedItems.toTypedArray()) }
    }

    val groupList = itemRepo.getGroupList()
    fun getAllItemsInGroup(groupId: Long) = itemRepo.getAllItemsInGroup(groupId)

    private var editType: MutableLiveData<Int> = MutableLiveData()

    init {
        editType.value = TYPE_ADD
    }

    fun clearCheckedItems(showUndoSnackbar: (Int, () -> Unit) -> Unit) {
        recentClearedItems = allCheckedItems.value.orEmpty()
        for (item in recentClearedItems) {
            item.cleared = true
        }
        scope.launch { update(*recentClearedItems.toTypedArray()) }

        //make snackbar with undo button when recentClearedItems.isNotEmpty()
        //if button is clicked, then vm.undoClear()
        showUndoSnackbar(recentClearedItems.size, ::undoClear)
    }

    fun tabPosToGroupId(pos: Int): Long =
        if (pos - POSITION_OFFSET < 0) (pos - POSITION_OFFSET).toLong()
        else groupList.value!![pos - POSITION_OFFSET].id!!

    companion object {
        private var vm: MainActivityViewModel? = null
        fun create(activity: FragmentActivity): MainActivityViewModel =
            if (vm === null) ViewModelProviders.of(activity).get(
                MainActivityViewModel::class.java
            )
            else vm!!
    }
}