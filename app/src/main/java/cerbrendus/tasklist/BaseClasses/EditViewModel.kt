package cerbrendus.tasklist.BaseClasses

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cerbrendus.tasklist.Database.ItemRepository
import cerbrendus.tasklist.dataClasses.Group

//Created by Brendan on 30-12-2018.
abstract class EditViewModel(application: Application) : AndroidViewModel(application) {
    val itemRepo = ItemRepository.create(application)

    val editType : MutableLiveData<Int> = MutableLiveData()
    var openedAsView = false
    var isCopy = false
    lateinit var groupTitlesList : List<String>
    lateinit var groupList : List<Group>

    lateinit var intent : Intent

    init {
        editType.value = TYPE_ADD
    }

    /**  Configures the ViewModel according to the intent.
    *   Returns 'true' if it was successful, otherwise 'false'
    *
    *   Handles: editType set; isCopy set; openedAsView set; groupList set.
    */
    open fun configure(_intent : Intent) : Boolean {
        // Set the intent
        this.intent = _intent

        // Get the editType value
        editType.value = intent.getIntExtra(TYPE_INTENT_KEY, TYPE_ADD)
        isCopy = intent.getBooleanExtra(COPIED_KEY,false)

        //Set check-value for Activity opened in view mode
        openedAsView = (editType.value == TYPE_VIEW)

        return true
    }

    /** Returns the group object from the groupList based on id */
    fun getGroupFromId(id : Long) : Group? = groupList.firstOrNull{id == it.id}

    fun save() : Boolean {
        return when(editType.value){
            TYPE_ADD -> handleAdded()
            TYPE_UPDATE -> handleUpdated()
            else -> false
        }
    }

    /** Handles the updating of an item. Should return success */
    abstract fun handleUpdated() : Boolean
    /** Handles the adding of an item. Should return success */
    abstract fun handleAdded() : Boolean
    /** Handle the deletion of an item. Should return success */
    abstract fun handleDeleted() : Boolean

    fun isInvalidText(text: String?) : Boolean = (text.equals("") || text.equals(null))
}

abstract class EditItemViewModel(application: Application) : EditViewModel(application) {

    override fun configure(intent : Intent) : Boolean {
        super.configure(intent)

        //Set groupList
        groupList = intent.getParcelableArrayListExtra(GROUPLIST_KEY) ?: itemRepo.getGroupList().value ?: listOf()
        groupTitlesList = groupList.map{it ->  it.title ?: ""}

        return true
    }

    /** Sets the current groupId */
    abstract fun setGroupId(selectedGroupId : Long)
}