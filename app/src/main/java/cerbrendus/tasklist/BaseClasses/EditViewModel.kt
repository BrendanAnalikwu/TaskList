package cerbrendus.tasklist.BaseClasses

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cerbrendus.tasklist.Database.ItemRepository
import cerbrendus.tasklist.EditTaskItem.CURRENT_GROUP_ID_KEY
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.Group

const val TYPE_INTENT_KEY = "cerbrendus.tasklist.Edit.TYPE_INTENT_KEY"
const val TYPE_ADD = 0
const val TYPE_UPDATE = 1
const val TYPE_VIEW = 2
const val COPIED_KEY = "cerbrendus.tasklist.Edit.COPIED_KEY"
const val GROUPLIST_KEY = "cerbrendus.tasklist.Edit.GROUPLIST_KEY"

//Created by Brendan on 30-12-2018.
abstract class EditViewModel(application: Application) : AndroidViewModel(application) {
    val itemRepo = ItemRepository.create(application)

    val editType: MutableLiveData<Int> = MutableLiveData()
    var openedAsView = false
    var isCopy = false

    lateinit var intent: Intent

    init {
        editType.value = TYPE_ADD
    }

    /**  Configures the ViewModel according to the intent.
     *   Returns 'true' if it was successful, otherwise 'false'
     *
     *   Handles: editType set; isCopy set; openedAsView set; groupList set.
     */
    open fun configure(_intent: Intent): Boolean {
        // Set the intent
        this.intent = _intent

        // Get the editType value
        editType.value = intent.getIntExtra(TYPE_INTENT_KEY, TYPE_ADD)
        isCopy = intent.getBooleanExtra(COPIED_KEY, false)

        //Set check-value for Activity opened in view mode
        openedAsView = (editType.value == TYPE_VIEW)

        return true
    }

    suspend fun save(): Long? {
        return when (editType.value) {
            TYPE_ADD -> handleAdded()
            TYPE_UPDATE -> {
                handleUpdated(); null
            }
            else -> null
        }
    }

    /** Handles the updating of an item. Should return success */
    abstract suspend fun handleUpdated(): Boolean

    /** Handles the adding of an item. Should return success */
    abstract suspend fun handleAdded(): Long?

    /** Handle the deletion of an item. Should return success */
    abstract suspend fun handleDeleted(): Boolean

    suspend fun updateChecked(id: Long, bool: Boolean) {
        itemRepo.updateChecked(id, bool)
    }

    fun isInvalidText(text: String?): Boolean = (text.equals("") || text.equals(null))

    fun colorNameOf(color: Int): String {
        val r = getApplication<Application>().resources
        val i = r.getIntArray(R.array.colorArray).indexOf(color)
        return if (i != -1) r.getStringArray(R.array.colorNameArray)[i]
        else r.getString(R.string.unnamed_color)
    }
}

/** The ViewModel meant for activities involved with editing items in the to do list.
 * Implements the setting of a group and the pre-setting of the group.*/
abstract class EditItemViewModel(application: Application) : EditViewModel(application) {
    lateinit var groupTitlesList: List<String>
    lateinit var groupList: List<Group>

    override fun configure(_intent: Intent): Boolean {
        super.configure(_intent)

        //Set groupList
        groupList = _intent.getParcelableArrayListExtra(GROUPLIST_KEY) ?: itemRepo.getGroupList().value ?: listOf()
        groupTitlesList = groupList.map { it -> it.title ?: "" }

        // Pre-set the group when adding item from group tab
        val preGroupId = intent.getLongExtra(CURRENT_GROUP_ID_KEY, -1)
        if (preGroupId >= 0 && editType.value == TYPE_ADD) setGroupId(preGroupId)

        return true
    }

    /** Sets the current groupId */
    abstract fun setGroupId(selectedGroupId: Long)

    /** Returns the group object from the groupList based on id */
    fun getGroupFromId(id: Long): Group? = groupList.firstOrNull { id == it.id }

    abstract fun handleResult(requestCode: Int, resultCode: Int, data: Intent?)
}