package cerbrendus.tasklist.Main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.EditTaskItem.*
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem

class ItemAdapter(_taskList: List<TaskItem>, _context: FragmentActivity) : RecyclerView.Adapter<TaskHolder>()  {

    private var taskList = _taskList
    private val context = _context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val itemView : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item,parent,false)
        return TaskHolder(itemView)
    }

    override fun getItemCount(): Int =  taskList.size

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        Log.i("check","view replaced. It was ${holder.titleTV.text} which has checked ${holder.checkTV.isChecked}")
        holder.checkTV.setOnCheckedChangeListener{_,_->}
        val task : TaskItem = taskList[position]
        holder.titleTV.text = task.title
        holder.checkTV.isChecked = task.checked

        //Set checkbox check listener
        holder.checkTV.setOnCheckedChangeListener { _,bool->
            task.checked = bool
            MainActivityViewModel.create(context).update(task)
        }

        //Set item onClickListener
        holder.view.setOnClickListener {
            val intent = Intent(context, EditTaskActivity::class.java).apply{
                putExtra(TYPE_INTENT_KEY, TYPE_VIEW)
                putExtra(TASK_ITEM_KEY, taskList.get(position))
                try {putParcelableArrayListExtra(GROUPLIST_KEY,ArrayList(MainActivityViewModel.create(context).groupList.value!!))} catch (e: NullPointerException) {}
            }
            context.startActivity(intent) }
    }

    fun setItems(_taskList: List<TaskItem>){
        //TODO("implement comparison between taskList and _taskList")
        taskList = _taskList
        notifyDataSetChanged() //TODO("replace by correct notifier corresponding to change in list")
    }

    fun onItemMoved(from: Int, to: Int) {
        //ToDo: save item position to database
        val list = taskList.toMutableList()
        if (from < to) {
            for (i in from..(to-1)){ list[i] = taskList[i+1] }
            list[to] = taskList[from]
        }
        else {
            for(i in (to+1)..from) { list[i] = taskList[i-1] }
            list[to] = taskList[from]
        }
        taskList = list.toList()
        notifyItemMoved(from,to)
    }

}

class TaskHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    val view = itemView
    val titleTV = itemView.findViewById<TextView>(R.id.task_title)!!
    val checkTV = itemView.findViewById<CheckBox>(R.id.attribute_icon)!!
}