package cerbrendus.tasklist.BaseClasses

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.R

const val VIEWTYPE_TEXT = 0

abstract class EditAdapter(_context: FragmentActivity)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var attributeList : List<Any> = listOf()

    val context = _context
    abstract val vm : EditViewModel

    fun handleDataChanged() {
        attributeList = makeAttributeList()
        notifyDataSetChanged()
    }

    abstract fun makeAttributeList() : List<Any>

    override fun getItemCount(): Int = attributeList.size

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            VIEWTYPE_TEXT -> {
                val viewHolder = holder as AttributeTextViewHolder
                val attribute = attributeList[position] as AttributeText
                viewHolder.icon?.setImageDrawable(attribute.drawable)
                viewHolder.title?.text = attribute.text
                viewHolder.view.setOnClickListener {
                    if((vm.editType.value == TYPE_UPDATE) or (vm.editType.value == TYPE_ADD)) attribute.selector()
                }
                //if (!passGroupTitleTextView(viewHolder.title)) throw(Exception("Activity not loaded, but adapter is..."))
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when(position){
        0 -> VIEWTYPE_TEXT
        else -> VIEWTYPE_TEXT //TODO: implement empty/plain ViewHolder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val attributeHolder = when(viewType) {
            VIEWTYPE_TEXT -> AttributeTextViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.attribute_list_text_item, parent, false)
            )
            else -> AttributeTextViewHolder( //TODO: implement empty/plain ViewHolder
                LayoutInflater.from(parent.context).inflate(
                    R.layout.attribute_list_text_item, parent, false)
            )
        }
        return attributeHolder
    }
}

class AttributeTextViewHolder(attributeView: View) : RecyclerView.ViewHolder(attributeView){
    val view = attributeView
    val icon : ImageView? = view.findViewById<ImageView?>(R.id.attribute_icon)
    val title = view.findViewById<TextView?>(R.id.attribute_title)
}

class AttributeText(val text : String, val drawable : Drawable, val selector: () -> Unit){ val viewType = VIEWTYPE_TEXT}