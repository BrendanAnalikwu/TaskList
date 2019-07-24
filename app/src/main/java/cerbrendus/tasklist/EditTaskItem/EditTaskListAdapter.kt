package cerbrendus.tasklist.EditTaskItem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.BaseClasses.*
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem

const val POS_SUBLIST = 1
const val VIEWTYPE_SUBLIST = 10

class EditTaskListAdapter(
    _context: FragmentActivity, viewModel: EditViewModel,
    private val openGroupSelector: () -> Unit
) : EditAdapter(_context) {

//    private val context = _context
//    val vm = EditTaskViewModel.create(context)
//    var passGroupTitleTextView : (TextView?) -> Boolean = {false}

    //override fun getItemCount(): Int = numAttributes

    override val vm = viewModel as EditTaskViewModel

    override fun makeAttributeList(): List<BaseAttribute> {
        val list = mutableListOf<BaseAttribute>(
            AttributeText(
                vm.getGroupFromId(vm.currentItem.value!!.group_id)?.title
                    ?: context.getString(R.string.no_group_selected),
                context.getDrawable(R.drawable.ic_list)!!,
                openGroupSelector,
                vm.getGroupFromId(vm.currentItem.value!!.group_id)?.color
            )
        )
        if (vm.currentItem.value?.isSublistItem == true) list.add(
            AttributeSwitch(
                "Display task outside sublist",
                vm.currentItem.value!!.visible
            ) { bool ->
                vm.currentItem.value = vm.currentItem.value!!.apply { visible = bool }
            })
        else list.add(
            AttributeSublist(
                vm.sublist.value.orEmpty(),
                vm.editType.value != TYPE_VIEW
            )
        )
        return list
    }

    fun handleSublistChanged() {
        attributeList = makeAttributeList()
        notifyItemChanged(POS_SUBLIST)
    }

    override fun customOnCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        VIEWTYPE_SUBLIST -> AttributeSublistViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.attribute_list_sublist_item, parent, false
            )
        )
        else -> super.customOnCreateViewHolder(parent, viewType)
    }

    override fun customOnBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEWTYPE_SUBLIST -> {
                val viewHolder = holder as AttributeSublistViewHolder
                val attribute = attributeList[position] as AttributeSublist

                viewHolder.recyclerView?.layoutManager = LinearLayoutManager(context)
                viewHolder.recyclerView?.setHasFixedSize(true)
                viewHolder.recyclerView?.adapter =
                    SublistAdapter(
                        attribute.list,
                        context as EditTaskActivity,
                        attribute.displayAdd
                    )
            }
        }
    }

//    @SuppressLint("ResourceType")
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when(getItemViewType(position)){
//            VIEWTYPE_TEXT -> {
//                val viewHolder = holder as AttributeTextViewHolder
//                val attribute = attributeList[position] as AttributeText
//                viewHolder.icon?.setImageDrawable(attribute.drawable)
//                viewHolder.title?.text = attribute.text
//                viewHolder.view.setOnClickListener {
//                    if((vm.editType.value == TYPE_UPDATE) or (vm.editType.value == TYPE_ADD)) openGroupSelector()
//                }
//                //if (!passGroupTitleTextView(viewHolder.title)) throw(Exception("Activity not loaded, but adapter is..."))
//            }
//        }
//    }

//    override fun getItemViewType(position: Int): Int = when(position){
//        0 -> VIEWTYPE_TEXT
//        else -> VIEWTYPE_TEXT
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val attributeHolder = when(viewType) {
//            VIEWTYPE_TEXT -> AttributeTextViewHolder(
//                LayoutInflater.from(parent.context).inflate(R.layout.attribute_list_text_item, parent, false)
//            )
//            else -> AttributeTextViewHolder(
//                LayoutInflater.from(parent.context).inflate(
//                    R.layout.attribute_list_text_item,
//                    parent,
//                    false
//                )
//            )
//        }
//        return attributeHolder
//    }

//    fun setGroupTitleSetup(function : (TextView?) -> Boolean) {passGroupTitleTextView = function}

}

class AttributeSublistViewHolder(attributeView: View) : RecyclerView.ViewHolder(attributeView) {
    val view = attributeView
    val recyclerView = view.findViewById<RecyclerView?>(R.id.attribute_sublist_recyclerview)
}

class AttributeSublist(val list: List<TaskItem>, val displayAdd: Boolean) : BaseAttribute(VIEWTYPE_SUBLIST)