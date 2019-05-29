package cerbrendus.tasklist.EditTaskItem

import android.app.Application
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import cerbrendus.tasklist.BaseClasses.EditItemViewModel
import cerbrendus.tasklist.BaseClasses.TYPE_ADD
import cerbrendus.tasklist.BaseClasses.TYPE_VIEW
import cerbrendus.tasklist.dataClasses.TaskItem

const val ITEM_LIST_KEY = "cerbrendus.tasklist.Edit.ITEM_LIST_KEY"

//Created by Brendan on 30-12-2018.
class EditTaskViewModel(application: Application) : EditItemViewModel(application) {
//    private val itemRepo = ItemRepository.create(application)

    fun insert(vararg item: TaskItem) {itemRepo.insert(*item)}
    fun update(vararg item: TaskItem) {itemRepo.update(*item)}
    fun delete(vararg item: TaskItem) {itemRepo.delete(*item)}

//    val editType : MutableLiveData<Int> = MutableLiveData()
//    var openedAsView = false
//    var itemIsCopy = false
//    lateinit var groupTitlesList : List<String>
//    lateinit var groupList : List<Group>
    var currentItem : MutableLiveData< TaskItem > = MutableLiveData()

//    lateinit var intent : Intent

    init {
        currentItem.value = TaskItem() //TODO: Move to configure. Why again?
    }

    /**  Configures the ViewModel according to the intent.
     *   Returns 'true' if it was successful, otherwise 'false' */
    override fun configure(_intent : Intent) : Boolean {
        super.configure(_intent)
        // Set the intent
//        this.intent = _intent

        // Get the editType value
//        editType.value = intent.getIntExtra(TYPE_INTENT_KEY, TYPE_ADD)
//        itemIsCopy = intent.getBooleanExtra(COPIED_KEY,false)

        // If not passed, currentItem set to empty item, if it should be passed return false
        currentItem.value = intent.getParcelableExtra(TASK_ITEM_KEY) ?:
                if (editType.value == TYPE_ADD && !isCopy) TaskItem() else return false

        if (isCopy) currentItem.value?.id = null

        //Set check-value for Activity opened in view mode
//        openedAsView = (editType.value == TYPE_VIEW)

        //Set groupList
//        groupList = intent.getParcelableArrayListExtra(GROUPLIST_KEY) ?: itemRepo.getGroupList().value ?: listOf()
//        groupTitlesList = groupList.map{it ->  it.title ?: ""}

        return true
    }

//    fun getGroupFromId(id : Long) : Group? = groupList.firstOrNull{id == it.id}
//
//    fun save() : Boolean {
//        return when(editType.value){
//            TYPE_ADD -> handleItemAdded()
//            TYPE_UPDATE -> handleItemUpdated()
//            else -> false
//        }
//    }

    //Handle different  edit actions (update, add, delete)
    override fun handleUpdated() : Boolean {
        update(currentItem.value!!)
        editType.value = TYPE_VIEW
        return true
    }

    override fun handleAdded() : Boolean {
        insert(currentItem.value!!)
        return true
    }

    override fun handleDeleted(): Boolean {
        delete(currentItem.value!!)
        return true
    }

    override fun setGroupId(selectedGroupId : Long) {
        currentItem.value = currentItem.value?.apply { group_id = selectedGroupId }
    }

//    fun isInvalidText(text: String?) : Boolean = (text.equals("") || text.equals(null))

    companion object {
        private var vm: EditTaskViewModel? = null
        fun create(activity: FragmentActivity): EditTaskViewModel =
            if(vm ===null) ViewModelProviders.of(activity).get(
                EditTaskViewModel::class.java)
            else vm!!
    }
}