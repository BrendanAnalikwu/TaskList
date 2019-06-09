package cerbrendus.tasklist.Main

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.BaseClasses.GROUPLIST_KEY
import cerbrendus.tasklist.BaseClasses.TYPE_INTENT_KEY
import cerbrendus.tasklist.BaseClasses.TYPE_VIEW
import cerbrendus.tasklist.EditTaskItem.EditTaskActivity
import cerbrendus.tasklist.EditTaskItem.TASK_ITEM_KEY
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem


class ItemAdapter(_taskList: List<TaskItem>, _context: ListFragment) : RecyclerView.Adapter<TaskHolder>()  {

    private var taskList = _taskList
    private val contextA = _context.activity!!
    private val contextF = _context
    private val vm = ListFragmentViewModel.create(contextF)
    private val defaultColor = contextA.getColor(R.color.colorAccent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val itemView : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item,parent,false)
        val taskHolder = TaskHolder(itemView)
        taskHolder.checkTV.supportButtonTintList =  ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(

                Color.DKGRAY, vm.getGroup()?.color ?: defaultColor
            ))
        return taskHolder
    }

    override fun getItemCount(): Int =  taskList.size

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        Log.i("check","view replaced. It was ${holder.titleTV.text} which has checked ${holder.checkTV.isChecked}")
        holder.checkTV.setOnCheckedChangeListener{_,_->}
        val task : TaskItem = taskList[position]
        holder.titleTV.text = "${task.title} (${task.priority})"
        holder.checkTV.isChecked = task.checked

        //Set checkbox check listener
        holder.checkTV.setOnCheckedChangeListener { _,bool->
            task.checked = bool
            ListFragmentViewModel.create(contextF).updateChecked(task.id!!,bool)
        }

        //Set item onClickListener
        holder.view.setOnClickListener {
            val intent = Intent(contextA, EditTaskActivity::class.java).apply{
                putExtra(TYPE_INTENT_KEY, TYPE_VIEW)
                putExtra(TASK_ITEM_KEY, taskList.get(position))
                try {putParcelableArrayListExtra(GROUPLIST_KEY,ArrayList(MainActivityViewModel.create(contextA).groupList.value!!))} catch (e: NullPointerException) {}
            }
            contextA.startActivity(intent) }
    }

    fun onItemMoved(_taskList: List<TaskItem>, from: Int, to: Int) {
        taskList = _taskList.toList()
        notifyItemMoved(from,to)
    }

    fun onItemChanged(_taskList: List<TaskItem>, startPos: Int, count: Int) {
        taskList = _taskList.toList()
        notifyItemRangeChanged(startPos, count,null)
    }

    fun onItemInserted(_taskList: List<TaskItem>, pos: Int) {
        taskList = _taskList.toList()
        notifyItemInserted(pos)
    }

    fun onItemDelete(_taskList: List<TaskItem>,pos: Int) {
        taskList = _taskList.toList()
        notifyItemRemoved(pos)
    }

    fun onDatasetChanged(_taskList: List<TaskItem>) {
        taskList = _taskList.toList()
        notifyDataSetChanged()
    }

    fun setData(_taskList: List<TaskItem>) {
        taskList = _taskList.toList()
    }

}

class TaskHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    val view = itemView
    val titleTV = itemView.findViewById<TextView>(R.id.task_title)!!
    val checkTV = itemView.findViewById<AppCompatCheckBox>(R.id.attribute_icon)!!
}