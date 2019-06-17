package cerbrendus.tasklist.BaseClasses

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.EditGroup.EditGroupViewModel
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem

const val VIEWTYPE_TEXT = 0
const val VIEWTYPE_COLOR = 1
const val VIEWTYPE_SUBLIST = 2

abstract class EditAdapter(_context: FragmentActivity)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var attributeList : List<BaseAttribute> = listOf()

    val context = _context
    abstract val vm : EditViewModel

    fun handleDataChanged() {
        attributeList = makeAttributeList()
        notifyDataSetChanged()
    }

    abstract fun makeAttributeList() : List<BaseAttribute>

    override fun getItemCount(): Int = attributeList.size

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            VIEWTYPE_TEXT -> {
                val viewHolder = holder as AttributeTextViewHolder
                val attribute = attributeList[position] as AttributeText
                viewHolder.icon?.setImageDrawable(attribute.drawable)
                viewHolder.icon?.setColorFilter(attribute.color?: Color.DKGRAY)
                viewHolder.title?.text = attribute.text
                viewHolder.view.setOnClickListener {
                    if((vm.editType.value == TYPE_UPDATE) or (vm.editType.value == TYPE_ADD)) attribute.selector()
                }
                //if (!passGroupTitleTextView(viewHolder.title)) throw(Exception("Activity not loaded, but adapter is..."))
            }
            VIEWTYPE_COLOR -> {
                val viewHolder = holder as AttributeColorViewHolder
                val attribute = attributeList[position] as AttributeColor
                viewHolder.title?.text = attribute.text
                viewHolder.colorSquare?.setBackgroundColor(attribute.color)
                viewHolder.view.setOnClickListener {
                    if((vm.editType.value == TYPE_UPDATE) or (vm.editType.value == TYPE_ADD)) SelectColorDialog().show(context.supportFragmentManager,"colorDialog")
                }
            }
            VIEWTYPE_SUBLIST -> {
                val viewHolder = holder as AttributeSublistViewHolder
                val attribute = attributeList[position] as AttributeSublist

                viewHolder.recyclerView?.layoutManager = LinearLayoutManager(context)
                viewHolder.recyclerView?.setHasFixedSize(true)
                viewHolder.recyclerView?.adapter = SublistAdapter(attribute.list,context,attribute.displayAdd)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = attributeList[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType) {
        VIEWTYPE_TEXT -> AttributeTextViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.attribute_list_text_item, parent, false)
        )
        VIEWTYPE_COLOR -> AttributeColorViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.attribute_list_color_item, parent, false)
        )
        VIEWTYPE_SUBLIST -> AttributeSublistViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.attribute_list_sublist_item, parent, false)
        )
        else -> AttributeTextViewHolder( //TODO: implement empty/plain ViewHolder
            LayoutInflater.from(parent.context).inflate(
                R.layout.attribute_list_text_item, parent, false)
        )
    }
}

class AttributeTextViewHolder(attributeView: View) : RecyclerView.ViewHolder(attributeView){
    val view = attributeView
    val icon : ImageView? = view.findViewById<ImageView?>(R.id.attribute_icon)
    val title = view.findViewById<TextView?>(R.id.attribute_title)
}

class AttributeColorViewHolder(attributeView: View) : RecyclerView.ViewHolder(attributeView){
    val view = attributeView
    val title = view.findViewById<TextView?>(R.id.attribute_title)
    val colorSquare : View? = view.findViewById<View?>(R.id.attribute_color_square)
}

class AttributeSublistViewHolder(attributeView: View) : RecyclerView.ViewHolder(attributeView){
    val view = attributeView
    val recyclerView = view.findViewById<RecyclerView?>(R.id.attribute_sublist_recyclerview)
}

abstract class BaseAttribute(val viewType: Int)
class AttributeText(val text : String, val drawable : Drawable, val selector: () -> Unit, val color: Int? = null) : BaseAttribute(VIEWTYPE_TEXT)
class AttributeColor(val text : String, @ColorInt val color : Int) : BaseAttribute(VIEWTYPE_COLOR)
class AttributeSublist(val list : List<TaskItem>, val displayAdd : Boolean) : BaseAttribute(VIEWTYPE_SUBLIST)


@SuppressLint("ValidFragment")
class SelectColorDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val vm = EditGroupViewModel.create(it) //TODO: fix
            val inflater = requireActivity().layoutInflater

            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.select_color))
            val view = inflater.inflate(R.layout.color_select_dialog, null)
            builder.setView(view)
            view.findViewById<RecyclerView?>(R.id.select_color_recyclerview)
                ?.apply {
                    layoutManager = LinearLayoutManager(this@SelectColorDialog.context)
                    adapter = SelectColorAdapter(it,vm.colorList,vm.colorNameList,vm::selectColor,{dialog?.dismiss()},inflater)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    class SelectColorAdapter(_context: FragmentActivity, _colorList: List<Int>, _colorNameList: List<String>,
                             val selector : (color: Int) -> Unit, val closer : () -> Unit,
                             val inflater: LayoutInflater) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val colorList = _colorList
        private val colorNameList = _colorNameList
        val context = _context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            AttributeColorViewHolder(inflater.inflate(
                R.layout.attribute_list_color_item, parent, false))

        override fun getItemCount(): Int = colorList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as AttributeColorViewHolder).colorSquare?.setBackgroundColor(colorList[position])
            (holder as AttributeColorViewHolder).title?.text = colorNameList[position]
            holder.view.setOnClickListener { selector(colorList[position]); closer() }
        }
    }
}