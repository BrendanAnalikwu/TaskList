package cerbrendus.tasklist.EditTaskItem

import android.app.Application
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import cerbrendus.tasklist.Database.ItemRepository
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem

//Created by Brendan on 30-12-2018.
class EditViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = ItemRepository.create(application)

    fun insert(vararg item: TaskItem) {itemRepo.insert(*item)}
    fun update(vararg item: TaskItem) {itemRepo.update(*item)}
    fun delete(vararg item: TaskItem) {itemRepo.delete(*item)}

    val editType : MutableLiveData<Int> = MutableLiveData()
    var openedAsView = false
    var itemIsCopy = false
    lateinit var groupTitlesList : List<String>
    lateinit var groupList : List<Group>
    var currentItem : MutableLiveData< TaskItem > = MutableLiveData()

    lateinit var intent : Intent

    init {
        editType.value = TYPE_ADD
        currentItem.value = TaskItem()
    }

    fun configure(_intent : Intent) : Boolean {
        /*  Configures the ViewModel according to the intent.
        *   Returns 'true' if it was successful, otherwise 'false' */

        // Set the intent
        this.intent = _intent

        // Get the editType value
        editType.value = intent.getIntExtra(TYPE_INTENT_KEY, TYPE_ADD)
        itemIsCopy = intent.getBooleanExtra(TASK_COPIED_KEY,false)

        // If not passed, currentItem set to empty item, if it should be passed return false
        currentItem.value = intent.getParcelableExtra(TASK_ITEM_KEY) ?:
                if (editType.value == TYPE_ADD && !itemIsCopy) TaskItem() else return false

        if (itemIsCopy) currentItem.value?.id = null

        //Set check-value for Activity opened in view mode
        openedAsView = (editType.value == TYPE_VIEW)

        //Set groupList
        groupList = intent.getParcelableArrayListExtra(GROUPLIST_KEY) ?: itemRepo.getGroupList().value ?: listOf()
        groupTitlesList = groupList.map{it ->  it.title ?: ""}

        // Pre-set the group when adding item from group tab
        val preGroupId = intent.getLongExtra(CURRENT_GROUP_ID_KEY,-1)
        if (preGroupId >= 0 && editType.value == TYPE_ADD) setGroupId(preGroupId)

        return true
    }

    fun getGroupFromId(id : Long) : Group? = groupList.firstOrNull{id == it.id}

    fun save() : Boolean {
        return when(editType.value){
            TYPE_ADD -> handleItemAdded()
            TYPE_UPDATE -> handleItemUpdated()
            else -> false
        }
    }

    //Handle different  edit actions (update, add, delete)
    private fun handleItemUpdated() : Boolean {
        update(currentItem.value!!)
        editType.value = TYPE_VIEW
        return true
    }

    private fun handleItemAdded() : Boolean {
        insert(currentItem.value!!)
        return true
    }

    fun setGroupId(selectedGroupId : Long) {
        currentItem.run{value = value?.apply { group_id = selectedGroupId }}
    }

    fun isInvalidText(text: String?) : Boolean = (text.equals("") || text.equals(null))

    companion object {
        private var vm: EditViewModel? = null
        fun create(activity: FragmentActivity): EditViewModel =
            if(vm ===null) ViewModelProviders.of(activity).get(
                EditViewModel::class.java)
            else vm!!
    }
}