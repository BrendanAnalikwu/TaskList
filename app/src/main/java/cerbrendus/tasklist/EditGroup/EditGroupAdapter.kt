package cerbrendus.tasklist.EditGroup

import androidx.fragment.app.FragmentActivity
import cerbrendus.tasklist.BaseClasses.EditAdapter

const val ViewType_Text = 0
const val POS_GROUP = 0

class EditGroupAdapter(
    _context: FragmentActivity) : EditAdapter(_context){

    override val vm = EditGroupViewModel.create(context)

    override fun makeAttributeList(): List<Any> = listOf()
}