package cerbrendus.tasklist.BaseClasses

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cerbrendus.tasklist.Database.ItemRepository
import cerbrendus.tasklist.R

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