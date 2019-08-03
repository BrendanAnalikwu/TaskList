package cerbrendus.tasklist.Cleared

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProviders
import cerbrendus.tasklist.Database.ItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ClearedViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = ItemRepository.create(application)
    val allClearedItems = itemRepo.getAllCleared()
    val groupColorMap = Transformations.map(itemRepo.getGroupList()) {
        it.map { group -> Pair(group.id!!, group.color) }.toMap()
    }
    val scope = CoroutineScope(Dispatchers.IO)

    suspend fun unclearItem(pos: Int) {
        val item = allClearedItems.value.orEmpty()[pos]
        itemRepo.update(item.apply { cleared = false; clearedId = null })
    }

    suspend fun getGroupList() = itemRepo.getGroupListSuspend()


    companion object {
        private var vm: ClearedViewModel? = null
        fun create(activity: FragmentActivity): ClearedViewModel =
            if (vm === null) ViewModelProviders.of(activity).get(
                ClearedViewModel::class.java
            )
            else vm!!
    }
}