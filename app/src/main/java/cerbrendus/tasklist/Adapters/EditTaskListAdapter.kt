package cerbrendus.tasklist.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.R
import cerbrendus.tasklist.TYPE_ADD
import cerbrendus.tasklist.TYPE_UPDATE
import cerbrendus.tasklist.ViewModels.EditViewModel
import cerbrendus.tasklist.dataClasses.TaskItem
import java.lang.NullPointerException

const val numAttributes : Int  = 1
const val ViewType_Text = 0

class EditTaskListAdapter(
    _context: FragmentActivity,
    internal val openGroupSelector: (Boolean) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val context = _context
    val vm = EditViewModel.create(context)


//TODO: handle group deletion

    override fun getItemCount(): Int = numAttributes

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(position){
            0 -> {
                val viewHolder = holder as AttributeTextHolder
                viewHolder.icon?.setImageDrawable(context.getDrawable(R.drawable.design_password_eye))
                vm.currentItem.observe(context, Observer{ it ->
                    viewHolder.title?.text = try {vm.getGroupFromId(it.group_id!!.toInt())!!.title} catch(e: NullPointerException) {"No group selected"}
                })
                viewHolder.title?.text = try {vm.getGroupFromId(vm.currentItem.value?.group_id!!.toInt())?.title!!} catch (e: NullPointerException) {"No group selected"}
                viewHolder.view.setOnClickListener {
                    if((vm.editType.value == TYPE_UPDATE) or (vm.editType.value == TYPE_ADD)) openGroupSelector(true)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when(position){
        0 -> ViewType_Text
        else -> ViewType_Text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val attributeHolder = when(viewType) {
            ViewType_Text -> AttributeTextHolder(LayoutInflater.from(parent.context).inflate(R.layout.attribute_list_text_item, parent, false))
            else -> AttributeTextHolder(LayoutInflater.from(parent.context).inflate(R.layout.attribute_list_text_item, parent, false))
        }
        return attributeHolder
    }

}

class AttributeTextHolder(attributeView: View) : RecyclerView.ViewHolder(attributeView){
    val view = attributeView
    val icon : ImageView? = view.findViewById<ImageView?>(R.id.attribute_icon)
    val title = view.findViewById<TextView?>(R.id.attribute_title)
}