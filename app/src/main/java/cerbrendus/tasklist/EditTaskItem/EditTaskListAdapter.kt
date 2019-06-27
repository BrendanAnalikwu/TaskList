package cerbrendus.tasklist.EditTaskItem

import androidx.fragment.app.FragmentActivity
import cerbrendus.tasklist.BaseClasses.*
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem

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
        //val sublist = vm.currentItem.value?.getSublistAsList().orEmpty().toMutableList()
        //TODO: the above list is a list of Longs, we need to get the item itself from the id, and not add it to the list if the getter returns null
        val sublist = listOf(TaskItem("Hoihoi",1234),TaskItem("Ik houd heeeeeul veel van Chamon",1234),TaskItem("HAAAI",1234))
        return listOf(
            AttributeText(
                vm.getGroupFromId(vm.currentItem.value!!.group_id)?.title ?: context.getString(R.string.no_group_selected),
                context.getDrawable(R.drawable.ic_list)!!,
                openGroupSelector,
                vm.getGroupFromId(vm.currentItem.value!!.group_id)?.color
            ),
            AttributeSublist(
                sublist,
                vm.editType.value != TYPE_VIEW
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