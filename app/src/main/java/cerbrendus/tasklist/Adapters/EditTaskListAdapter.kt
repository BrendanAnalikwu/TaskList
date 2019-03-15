package cerbrendus.tasklist.Adapters

import android.annotation.SuppressLint
import android.util.Log
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
import org.w3c.dom.Text
import java.lang.NullPointerException

const val numAttributes : Int  = 1
const val ViewType_Text = 0
const val POS_GROUP = 0

class EditTaskListAdapter(
    _context: FragmentActivity,
    internal val openGroupSelector: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val context = _context
    val vm = EditViewModel.create(context)
    var setGroupTitle : (TextView?) -> Boolean = {false}//TODO: Implement


//TODO: handle group deletion

    override fun getItemCount(): Int = numAttributes

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(position){
            POS_GROUP -> {
                val viewHolder = holder as AttributeTextHolder
                viewHolder.icon?.setImageDrawable(context.getDrawable(R.drawable.design_password_eye))
                viewHolder.title?.text = "No group selecteddd"
                viewHolder.view.setOnClickListener {
                    if((vm.editType.value == TYPE_UPDATE) or (vm.editType.value == TYPE_ADD)) openGroupSelector()
                }
                setGroupTitle(viewHolder.title)
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

    fun setGroupTitleSetup(function : (TextView?) -> Boolean) {setGroupTitle = function}

}

class AttributeTextHolder(attributeView: View) : RecyclerView.ViewHolder(attributeView){
    val view = attributeView
    val icon : ImageView? = view.findViewById<ImageView?>(R.id.attribute_icon)
    val title = view.findViewById<TextView?>(R.id.attribute_title)
}