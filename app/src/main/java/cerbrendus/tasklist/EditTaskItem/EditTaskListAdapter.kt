package cerbrendus.tasklist.EditTaskItem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.BaseClasses.TYPE_ADD
import cerbrendus.tasklist.BaseClasses.TYPE_UPDATE
import cerbrendus.tasklist.R

const val numAttributes : Int  = 1
const val ViewType_Text = 0
const val POS_GROUP = 0

class EditTaskListAdapter(
    _context: FragmentActivity,
    internal val openGroupSelector: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val context = _context
    val vm = EditTaskViewModel.create(context)
    var passGroupTitleTextView : (TextView?) -> Boolean = {false}


    override fun getItemCount(): Int = numAttributes

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(position){
            POS_GROUP -> {
                val viewHolder = holder as AttributeTextHolder
                viewHolder.icon?.setImageDrawable(context.getDrawable(R.drawable.design_password_eye))
                viewHolder.title?.text = "No group selected"
                viewHolder.view.setOnClickListener {
                    if((vm.editType.value == TYPE_UPDATE) or (vm.editType.value == TYPE_ADD)) openGroupSelector()
                }
                if (!passGroupTitleTextView(viewHolder.title)) throw(Exception("Activity not loaded, but adapter is..."))
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when(position){
        0 -> ViewType_Text
        else -> ViewType_Text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val attributeHolder = when(viewType) {
            ViewType_Text -> AttributeTextHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.attribute_list_text_item, parent, false)
            )
            else -> AttributeTextHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.attribute_list_text_item,
                    parent,
                    false
                )
            )
        }
        return attributeHolder
    }

    fun setGroupTitleSetup(function : (TextView?) -> Boolean) {passGroupTitleTextView = function}

}

class AttributeTextHolder(attributeView: View) : RecyclerView.ViewHolder(attributeView){
    val view = attributeView
    val icon : ImageView? = view.findViewById<ImageView?>(R.id.attribute_icon)
    val title = view.findViewById<TextView?>(R.id.attribute_title)
}