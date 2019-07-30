package cerbrendus.tasklist.Cleared

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.Main.TaskHolder
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem

class ClearedItemAdapter(var list: List<TaskItem>, val context: ClearedActivity) : RecyclerView.Adapter<TaskHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.cleared_list_item, parent, false)
        val taskHolder = TaskHolder(itemView)
        taskHolder.checkTV.supportButtonTintList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(Color.DKGRAY, Color.DKGRAY/**context.getColor(R.color.colorAccent)*/)
        )
        taskHolder.checkTV.isChecked = true
        taskHolder.checkTV.isEnabled = false
        return taskHolder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        val task: TaskItem = list[position]
        holder.titleTV.text = task.title
    }

    fun setItems(_list : List<TaskItem>) {
        list = _list
        notifyDataSetChanged()
    }

}
