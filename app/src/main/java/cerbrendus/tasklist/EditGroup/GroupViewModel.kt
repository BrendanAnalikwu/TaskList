package cerbrendus.tasklist.EditGroup

import android.app.Application
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import cerbrendus.tasklist.Database.ItemRepository
import cerbrendus.tasklist.EditTaskItem.*
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem

//Created by Brendan on 30-12-2018.
class GroupViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = ItemRepository.create(application)

    fun createGroup(group: Group) {itemRepo.createGroup(group)}
    fun updateGroup(group: Group) {itemRepo.updateGroup(group)}
    fun deleteGroup(group: Group) {itemRepo.deleteGroup(group)}

    var editType : MutableLiveData<Int> = MutableLiveData()
    var openedAsView = false
    lateinit var intent : Intent
    var currentGroup : MutableLiveData<Group> = MutableLiveData()

    init {
        editType.value = TYPE_ADD
    }

    companion object {
        private var vm: GroupViewModel? = null
        fun create(activity: FragmentActivity): GroupViewModel =
            if(vm ===null) ViewModelProviders.of(activity).get(
                GroupViewModel::class.java)
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

        return true
    }
}