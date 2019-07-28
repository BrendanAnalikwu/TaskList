package cerbrendus.tasklist.Cleared

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProviders
import cerbrendus.tasklist.Database.ItemRepository

class ClearedViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepo = ItemRepository.create(application)
    val allClearedItems = itemRepo.getAllCleared()



    companion object {
        private var vm: ClearedViewModel? = null
        fun create(activity: FragmentActivity): ClearedViewModel =
            if (vm === null) ViewModelProviders.of(activity).get(
                ClearedViewModel::class.java
            )
            else vm!!
    }
}