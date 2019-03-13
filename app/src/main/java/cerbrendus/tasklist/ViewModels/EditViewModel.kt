package cerbrendus.tasklist.ViewModels

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import cerbrendus.tasklist.ItemRepository
import cerbrendus.tasklist.TYPE_ADD
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem

//Created by Brendan on 30-12-2018.
class EditViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = ItemRepository(application)

    fun insert(vararg item: TaskItem) {itemRepo.insert(*item)}
    fun update(vararg item: TaskItem) {itemRepo.update(*item)}
    fun delete(vararg item: TaskItem) {itemRepo.delete(*item)}

    val editType : MutableLiveData<Int> = MutableLiveData()
    var ETAOpenedAsView = false
    val groupTitlesList : LiveData<List<String>> = itemRepo.getGroupTitlesList()
    val groupList = itemRepo.getGroupList()
    var currentItem : MutableLiveData< TaskItem > = MutableLiveData()

    fun getGroupFromId(id : Int) : Group? = itemRepo.getGroupFromId(id)

    init { editType.value = TYPE_ADD
    }

    companion object {
        private var vm: EditViewModel? = null
        fun create(activity: FragmentActivity): EditViewModel =
            if(vm ===null) ViewModelProviders.of(activity).get(
                EditViewModel::class.java)
            else vm!!
    }
}