package cerbrendus.tasklist.ViewModels

import android.app.Application
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import cerbrendus.tasklist.*
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem

//Created by Brendan on 30-12-2018.
class EditViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = ItemRepository.create(application)

    fun insert(vararg item: TaskItem) {itemRepo.insert(*item)}
    fun update(vararg item: TaskItem) {itemRepo.update(*item)}
    fun delete(vararg item: TaskItem) {itemRepo.delete(*item)}

    val editType : MutableLiveData<Int> = MutableLiveData()
    var ETAOpenedAsView = false
    val groupTitlesList : LiveData<List<String>> = itemRepo.getGroupTitlesList()
    val groupList = itemRepo.getGroupList()
    var currentItem : MutableLiveData< TaskItem > = MutableLiveData()
    lateinit var intent : Intent

    init {
        editType.value = TYPE_ADD
        currentItem.value = TaskItem()
    }

    fun configure() : Boolean {
        /*  Configures the ViewModel according to the intent.
        *   Returns 'true' if it was successful, otherwise 'false' */

        // Get the editType value
        editType.value = intent.getIntExtra(TYPE_INTENT_KEY, TYPE_ADD)
        // If not passed, currentItem set to empty item, if it should be passed return false
        currentItem.value = intent.getParcelableExtra(TASK_ITEM_KEY) ?:
                if (editType.value == TYPE_ADD) TaskItem() else return false

        //Set check-value for Activity opened in view mode
        ETAOpenedAsView = (editType.value == TYPE_VIEW)

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
        update(currentItem.value!!)
        editType.value = TYPE_VIEW
        return true
    }

    private fun handleItemAdded() : Boolean {
        insert(currentItem.value!!)
        return true
    }

    fun getGroupFromId(id : Int) : Group? = itemRepo.getGroupFromId(id)

    fun isInvalidText(text: String?) : Boolean = (text.equals("") || text.equals(null))

    companion object {
        private var vm: EditViewModel? = null
        fun create(activity: FragmentActivity): EditViewModel =
            if(vm ===null) ViewModelProviders.of(activity).get(
                EditViewModel::class.java)
            else vm!!
    }
}