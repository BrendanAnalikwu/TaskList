package cerbrendus.tasklist.EditGroup

import androidx.fragment.app.FragmentActivity
import cerbrendus.tasklist.BaseClasses.AttributeColor
import cerbrendus.tasklist.BaseClasses.BaseAttribute
import cerbrendus.tasklist.BaseClasses.EditAdapter
import cerbrendus.tasklist.dataClasses.Group

class EditGroupAdapter(
    _context: FragmentActivity
) : EditAdapter(_context) {

    override val vm = EditGroupViewModel.create(context)

    override fun makeAttributeList(): List<BaseAttribute> = listOf(
        AttributeColor(
            vm.colorNameOf(vm.currentGroup.value?.color ?: Group().color),
            vm.currentGroup.value?.color ?: Group().color
        )
    )
}