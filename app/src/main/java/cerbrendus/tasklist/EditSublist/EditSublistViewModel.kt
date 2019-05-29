package cerbrendus.tasklist.EditSublist

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import cerbrendus.tasklist.BaseClasses.EditViewModel

class EditSublistViewModel(application: Application) : EditViewModel(application) {
    override fun handleUpdated(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleAdded(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleDeleted(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private var vm: EditSublistViewModel? = null
        fun create(activity: FragmentActivity): EditSublistViewModel =
            if(vm ===null) ViewModelProviders.of(activity).get(
                EditSublistViewModel::class.java)
            else vm!!
    }

}