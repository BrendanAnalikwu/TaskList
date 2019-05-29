package cerbrendus.tasklist.EditSublist

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import cerbrendus.tasklist.BaseClasses.BaseAttribute
import cerbrendus.tasklist.BaseClasses.EditAdapter
import cerbrendus.tasklist.BaseClasses.EditItemActivity
import cerbrendus.tasklist.BaseClasses.EditViewModel

class EditSublistActivity : EditItemActivity() {

    override lateinit var vm: EditSublistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        vm = EditSublistViewModel.create(this)
        super.onCreate(savedInstanceState)
    }

    override fun makeAdapter(): EditAdapter = EditSublistAdapter(this)

    override fun validateInputs(): Pair<Boolean, Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEditTypeChange(newType: Int) {
        super.onEditTypeChange(newType)
    }

    override fun handleItemCopied() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class EditSublistAdapter(_context: FragmentActivity) : EditAdapter(_context){
    override val vm: EditViewModel = EditSublistViewModel.create(_context)

    override fun makeAttributeList(): List<BaseAttribute> = listOf()

}