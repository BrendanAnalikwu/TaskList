package cerbrendus.tasklist.EditTaskItem

import androidx.fragment.app.FragmentActivity
import cerbrendus.tasklist.BaseClasses.AttributeText
import cerbrendus.tasklist.BaseClasses.BaseAttribute
import cerbrendus.tasklist.BaseClasses.EditAdapter
import cerbrendus.tasklist.BaseClasses.EditViewModel
import cerbrendus.tasklist.R

const val ViewType_Text = 0
const val POS_GROUP = 0

class EditTaskListAdapter(
    _context: FragmentActivity, viewModel: EditViewModel,
    internal val openGroupSelector: () -> Unit) : EditAdapter(_context){

//    private val context = _context
//    val vm = EditTaskViewModel.create(context)
//    var passGroupTitleTextView : (TextView?) -> Boolean = {false}

    //override fun getItemCount(): Int = numAttributes

    override val vm = viewModel as EditTaskViewModel

    override fun makeAttributeList(): List<BaseAttribute> {
        return listOf(
            AttributeText(
                vm.getGroupFromId(vm.currentItem.value!!.group_id)?.title ?: context.getString(R.string.no_group_selected),
                context.getDrawable(R.drawable.ic_list)!!,
                openGroupSelector,
                vm.getGroupFromId(vm.currentItem.value!!.group_id)?.color
            )
        )
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