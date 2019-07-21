package cerbrendus.tasklist.EditTaskItem

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import cerbrendus.tasklist.BaseClasses.EditItemViewModel
import cerbrendus.tasklist.BaseClasses.TASK_ITEM_REQUEST
import cerbrendus.tasklist.BaseClasses.TYPE_ADD
import cerbrendus.tasklist.dataClasses.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

const val ITEM_LIST_KEY = "cerbrendus.tasklist.Edit.ITEM_LIST_KEY"

//Created by Brendan on 30-12-2018.
class EditTaskViewModel(application: Application) : EditItemViewModel(application) {

    private suspend fun insertForResult(item: TaskItem) = itemRepo.insertForResult(item)
    private suspend fun update(vararg item: TaskItem) {
        itemRepo.update(*item)
    }

    private suspend fun delete(vararg item: TaskItem) {
        itemRepo.delete(*item)
    }

    val scope = CoroutineScope(Dispatchers.Default)
    var sublist = MutableLiveData<List<TaskItem>>() as LiveData<List<TaskItem>>

    var currentItem: MutableLiveData<TaskItem> = MutableLiveData()

    init {
        currentItem.value = TaskItem() //TODO: Move to configure. Why again?
    }

    /**  Configures the ViewModel according to the intent.
     *   Returns 'true' if it was successful, otherwise 'false' */
    override fun configure(_intent: Intent): Boolean {
        super.configure(_intent)
        // If not passed, currentItem set to empty item, if it should be passed return false
        currentItem.value = intent.getParcelableExtra(TASK_ITEM_KEY)
            ?: if (editType.value == TYPE_ADD && !isCopy) TaskItem() else return false

        if (isCopy) currentItem.value?.id = null

        return true
    }

    //Handle different  edit actions (update, add, delete)
    override suspend fun handleUpdated(): Boolean {
        update(currentItem.value!!)
        return true
    }

    override suspend fun handleAdded(): Long = insertForResult(currentItem.value!!)


    override suspend fun handleDeleted(): Boolean {
        delete(currentItem.value!!)
        return true
    }

    override fun setGroupId(selectedGroupId: Long) {
        currentItem.value = currentItem.value?.apply { group_id = selectedGroupId }
    }

    override fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TASK_ITEM_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val resultId = data?.getLongExtra(TASK_ITEM_KEY, -1) ?: -1
                val newList = currentItem.value!!.getSublistAsList().toMutableList()
                if (resultId > 0) {
                    newList.add(resultId)
                    Log.i("debug.tasklist.a","new subitem added: ${newList.size}")
                    currentItem.value = currentItem.value!!.apply { setSublistFromList(newList) }
                    //scope.launch { update(currentItem.value!!.apply { setSublistFromList(newList) }) }
                }
            }
        }
    }

    companion object {
        private var vm: EditTaskViewModel? = null
        fun create(activity: FragmentActivity): EditTaskViewModel =
            if (vm === null) ViewModelProviders.of(activity).get(
                EditTaskViewModel::class.java
            )
            else vm!!
    }
}