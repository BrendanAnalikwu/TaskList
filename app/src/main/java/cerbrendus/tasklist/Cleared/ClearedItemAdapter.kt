package cerbrendus.tasklist.Cleared

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.BaseClasses.GROUPLIST_KEY
import cerbrendus.tasklist.BaseClasses.TYPE_INTENT_KEY
import cerbrendus.tasklist.BaseClasses.TYPE_VIEW
import cerbrendus.tasklist.EditTaskItem.EditTaskActivity
import cerbrendus.tasklist.EditTaskItem.TASK_ITEM_KEY
import cerbrendus.tasklist.Main.TaskHolder
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem
import kotlinx.coroutines.launch

class ClearedItemAdapter(
    private var list: List<TaskItem>,
    private var colorMap: Map<Long, Int>,
    val context: ClearedActivity
) : RecyclerView.Adapter<TaskHolder>() {
    val vm = ClearedViewModel.create(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.cleared_list_item, parent, false)
        val taskHolder = TaskHolder(itemView)
        taskHolder.checkTV.supportButtonTintList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(
                Color.DKGRAY, context.getColor(R.color.colorAccent)
            )
        )
        taskHolder.checkTV.isChecked = true
        taskHolder.checkTV.isEnabled = false
        return taskHolder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        val task: TaskItem = list[position]
        holder.titleTV.text = "${task.title} (${task.clearedId})"
        holder.checkTV.supportButtonTintList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(
                Color.DKGRAY,
                if (colorMap.containsKey(task.group_id)) colorMap[task.group_id]!! else context.getColor(R.color.colorAccent)
            )
        )
        //Set item onClickListener
        holder.view.setOnClickListener {
            vm.scope.launch {
                val intent = Intent(context, EditTaskActivity::class.java).apply {
                    putExtra(TYPE_INTENT_KEY, TYPE_VIEW)
                    putExtra(TASK_ITEM_KEY, list[position])
                    try {
                        putParcelableArrayListExtra(
                            GROUPLIST_KEY,
                            ArrayList(vm.getGroupList())
                        )
                    } catch (e: NullPointerException) {
                    }
                }
                context.startActivity(intent)
            }
        }
    }

    fun setItems(_list: List<TaskItem>) {
        list = _list
        notifyDataSetChanged()
    }

    fun removeItem(_list: List<TaskItem>, pos: Int) {
        list = _list
        notifyItemRemoved(pos)
    }

    fun setColorMap(_map: Map<Long, Int>) {
        colorMap = _map
        notifyDataSetChanged()
    }

}
