package cerbrendus.tasklist.EditGroup

import androidx.fragment.app.FragmentActivity
import cerbrendus.tasklist.BaseClasses.AttributeColor
import cerbrendus.tasklist.BaseClasses.BaseAttribute
import cerbrendus.tasklist.BaseClasses.EditAdapter
import cerbrendus.tasklist.R

const val ViewType_Text = 0
const val POS_GROUP = 0

class EditGroupAdapter(
    _context: FragmentActivity) : EditAdapter(_context){

    override val vm = EditGroupViewModel.create(context)

    override fun makeAttributeList(): List<BaseAttribute> = listOf(AttributeColor(R.color.colorAccent))
}