package cerbrendus.tasklist.EditGroup

import android.app.Application
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import cerbrendus.tasklist.Database.ItemRepository
import cerbrendus.tasklist.EditTaskItem.TYPE_ADD
import cerbrendus.tasklist.EditTaskItem.TYPE_INTENT_KEY
import cerbrendus.tasklist.EditTaskItem.TYPE_UPDATE
import cerbrendus.tasklist.EditTaskItem.TYPE_VIEW
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem

//Created by Brendan on 30-12-2018.
class EditGroupViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = ItemRepository.create(application)

    fun createGroup(group: Group) {itemRepo.createGroup(group)}
    fun updateGroup(group: Group) {itemRepo.updateGroup(group)}
    fun deleteGroup(group: Group) {itemRepo.deleteGroup(group)}

    var editType : MutableLiveData<Int> = MutableLiveData()
    var openedAsView = false
    lateinit var intent : Intent
    var currentGroup : MutableLiveData<Group> = MutableLiveData()
    lateinit var itemList : List<TaskItem>

    init {
        editType.value = TYPE_ADD
    }

    companion object {
        private var vm: EditGroupViewModel? = null
        fun create(activity: FragmentActivity): EditGroupViewModel =
            if(vm ===null) ViewModelProviders.of(activity).get(
                EditGroupViewModel::class.java)
            else vm!!
    }

    fun configure(_intent : Intent) : Boolean {
        /*  Configures the ViewModel according to the intent.
        *   Returns 'true' if it was successful, otherwise 'false' */

        // Set the intent
        this.intent = _intent

        // Get the editType value
        editType.value = intent.getIntExtra(TYPE_INTENT_KEY, TYPE_ADD)

        // If not passed, currentGroup set to empty item, if it should be passed return false
        currentGroup.value = intent.getParcelableExtra<Group>(GROUP_KEY) ?:
                if (editType.value == TYPE_ADD) Group() else return false

        //Set check-value for Activity opened in view mode
        openedAsView = (editType.value == TYPE_VIEW)

        // Get itemList from intent or from repo or empty
        itemList = intent.getParcelableArrayListExtra(ITEM_LIST_KEY) ?: itemRepo.getAll().value.orEmpty()


        return true
    }

    fun save() : Boolean {
        return when(editType.value){
            TYPE_ADD -> handleItemAdded()
            TYPE_UPDATE -> handleItemUpdated()
            else -> false
        }
    }

    //Handle different  edit actions (update, add, delete)
    private fun handleItemUpdated() : Boolean {
        updateGroup(currentGroup.value!!)
        editType.value = TYPE_VIEW
        return true
    }

    private fun handleItemAdded() : Boolean {
        createGroup(currentGroup.value!!)
        return true
    }

    fun deleteItemsInGroup(group: Group) {
        itemRepo.delete(*itemList.filter { it.group_id == group.id }.toTypedArray())
    }

    fun isInvallidText(text: String): Boolean = (text.equals("") || text.equals(null))
}