package cerbrendus.tasklist.BaseClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem

const val VIEWTYPE_ITEM = 0
const val VIEWTYPE_ADD = 1

class SublistAdapter(private val itemList: List<TaskItem>, val context: FragmentActivity, val displayAdd : Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when(viewType) {
            VIEWTYPE_ITEM -> SublistViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.recyclerview_item, parent, false)
            )
            VIEWTYPE_ADD -> AddButtonViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.recyclerview_item, parent, false)
            )
            else ->  SublistViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.sublist_add_item_row, parent, false)
            )
        }

    override fun getItemCount(): Int = itemList.size + (if (displayAdd) 1 else 0)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEWTYPE_ITEM -> {
                val vh = holder as SublistViewHolder
                vh.title?.setText(itemList[position].title)
                vh.check?.isChecked = itemList[position].checked
                vh.check?.setOnCheckedChangeListener { _, bool ->
                    // TODO: Remove comment when the sublists contain actual items
                    /*(context as EditActivity).vm.updateChecked(itemList[position].id!!,bool)*/
                }
            }
            VIEWTYPE_ADD -> {
                (holder as AddButtonViewHolder).view.setOnClickListener {
                    // open EditTaskItem activity and pass the parent TaskItemId, set canHaveChildren to false
                    //TODO: create attribute 'canHaveChildren' or something similar.
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = if(displayAdd && position==itemList.size) VIEWTYPE_ADD else VIEWTYPE_ITEM

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