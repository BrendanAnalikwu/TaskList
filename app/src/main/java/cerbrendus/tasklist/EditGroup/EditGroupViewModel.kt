package cerbrendus.tasklist.EditGroup

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.annotation.ColorInt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import cerbrendus.tasklist.BaseClasses.EditViewModel
import cerbrendus.tasklist.BaseClasses.TYPE_ADD
import cerbrendus.tasklist.BaseClasses.TYPE_VIEW
import cerbrendus.tasklist.EditTaskItem.ITEM_LIST_KEY
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem

const val GROUP_KEY = "cerbrendus.tasklist.Edit.GROUP_KEY"

//Created by Brendan on 30-12-2018.
class EditGroupViewModel(application: Application) : EditViewModel(application) {

    fun createGroup(group: Group) {itemRepo.createGroup(group)}
    fun updateGroup(group: Group) {itemRepo.updateGroup(group)}
    fun deleteGroup(group: Group) {itemRepo.deleteGroup(group)}

    var currentGroup : MutableLiveData<Group> = MutableLiveData()
    lateinit var itemList : List<TaskItem>
    val colorList: List<Int> = application.resources.getIntArray(R.array.colorArray).toList()
    val colorNameList: List<String> = application.resources.getStringArray(R.array.colorNameArray).toList()

    /*  Configures the ViewModel according to the intent.
    *   Returns 'true' if it was successful, otherwise 'false' */
    override fun configure(_intent : Intent) : Boolean {
        super.configure(_intent)
        // If not passed, currentGroup set to empty item, if it should be passed return false
        currentGroup.value = intent.getParcelableExtra<Group>(GROUP_KEY) ?:
                if (editType.value == TYPE_ADD) Group() else return false

        // Get itemList from intent or from repo or empty
        itemList = intent.getParcelableArrayListExtra(ITEM_LIST_KEY) ?: itemRepo.getAll().value.orEmpty()


        return true
    }

    override fun handleUpdated(): Boolean {
        updateGroup(currentGroup.value!!)
        editType.value = TYPE_VIEW
        return true
    }

    override fun handleAdded() : Boolean {
        createGroup(currentGroup.value!!)
        return true
    }

    override fun handleDeleted(): Boolean {
        deleteGroup(currentGroup.value!!)
        return true
    }

    fun deleteItemsInGroup(group: Group) {
        itemRepo.delete(*itemList.filter { it.group_id == group.id }.toTypedArray())
    }

    fun selectColor(@ColorInt color: Int) {
        Log.i("tasklist.debug","color selected: $color")
        currentGroup.value = currentGroup.value?.apply { this.color = color }
    }

    fun isInvallidText(text: String): Boolean = (text.equals("") || text.equals(null))

    companion object {
        private var vm: EditGroupViewModel? = null
        fun create(activity: FragmentActivity): EditGroupViewModel =
            if(vm ===null) ViewModelProviders.of(activity).get(
                EditGroupViewModel::class.java)
            else vm!!
    }
}