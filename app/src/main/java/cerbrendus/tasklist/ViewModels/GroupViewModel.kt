package cerbrendus.tasklist.ViewModels

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import cerbrendus.tasklist.ItemRepository
import cerbrendus.tasklist.TYPE_ADD
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem

//Created by Brendan on 30-12-2018.
class GroupViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = ItemRepository(application)

    fun createGroup(group: Group) {itemRepo.createGroup(group)}
    fun updateGroup(group: Group) {itemRepo.updateGroup(group)}
    fun deleteGroup(group: Group) {itemRepo.deleteGroup(group)}

    var editType : MutableLiveData<Int> = MutableLiveData()
    var openedAsView = false

    init { editType.value = TYPE_ADD
    }

    companion object {
        private var vm: GroupViewModel? = null
        fun create(activity: FragmentActivity): GroupViewModel =
            if(vm ===null) ViewModelProviders.of(activity).get(
                GroupViewModel::class.java)
            else vm!!
    }
}