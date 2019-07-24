package cerbrendus.tasklist.EditTaskItem

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.BaseClasses.GROUPLIST_KEY
import cerbrendus.tasklist.BaseClasses.TYPE_ADD
import cerbrendus.tasklist.BaseClasses.TYPE_INTENT_KEY
import cerbrendus.tasklist.BaseClasses.TYPE_VIEW
import cerbrendus.tasklist.Main.MainActivityViewModel
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem
import kotlinx.coroutines.launch

const val VIEWTYPE_ITEM = 0
const val VIEWTYPE_ADD = 1

class SublistAdapter(
    private val itemList: List<TaskItem>,
    val context: EditTaskActivity,
    private val displayAdd: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val vm = context.vm

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            VIEWTYPE_ITEM -> SublistViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.recyclerview_item, parent, false
                )
            )
            VIEWTYPE_ADD -> AddButtonViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.recyclerview_item, parent, false
                )
            )
            else -> SublistViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.sublist_add_item_row, parent, false
                )
            )
        }

    override fun getItemCount(): Int = itemList.size + (if (displayAdd) 1 else 0)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEWTYPE_ITEM -> {
                val vh = holder as SublistViewHolder
                vh.title?.text = itemList[position].title
                if(vh.check?.isChecked != itemList[position].checked) vh.check?.isChecked = itemList[position].checked
                vh.check?.setOnCheckedChangeListener { _, bool ->
                    itemList[position].checked = bool
                    context.vm.run { scope.launch { updateChecked(itemList[position].id!!, bool) } }
                }

                vh.view.setOnClickListener {
                    val intent = Intent(context, EditTaskActivity::class.java).apply {
                        putExtra(TYPE_INTENT_KEY, TYPE_VIEW)
                        putExtra(TASK_ITEM_KEY, itemList[position])
                        try {
                            putParcelableArrayListExtra(
                                GROUPLIST_KEY,
                                ArrayList(MainActivityViewModel.create(context).groupList.value!!)
                            )
                        } catch (e: NullPointerException) {
                        }
                    }
                    context.startActivity(intent)
                    //context.openEditTaskActivity(TYPE_VIEW)
                }
            }
            VIEWTYPE_ADD -> {
                (holder as AddButtonViewHolder).view.setOnClickListener {
                    // open EditTaskItemActivity
                    context.openEditTaskActivity(TYPE_ADD, vm.currentItem.value?.group_id?:-1, true)
                    // when the new item is saved, have it be added to the sublist of the parent item
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (displayAdd && position == itemList.size) VIEWTYPE_ADD else VIEWTYPE_ITEM

}

class SublistViewHolder(attributeView: View) : RecyclerView.ViewHolder(attributeView) {
    val view = attributeView
    val title = view.findViewById<TextView?>(R.id.task_title)
    val check = view.findViewById<CheckBox?>(R.id.attribute_icon)
}

class AddButtonViewHolder(attributeView: View) : RecyclerView.ViewHolder(attributeView) {
    val view = attributeView
    val title = view.findViewById<TextView?>(R.id.task_title)
    val icon = view.findViewById<CheckBox?>(R.id.attribute_icon)
}