package cerbrendus.tasklist.EditTaskItem

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import cerbrendus.tasklist.BaseClasses.*
import cerbrendus.tasklist.dataClasses.TaskItem
import com.google.android.material.snackbar.Snackbar

const val TASK_ITEM_KEY = "cerbrendus.tasklist.Edit.TASK_ITEM_KEY"
const val CURRENT_GROUP_ID_KEY = "cerbrendus.tasklist.Edit.CURRENT_GROUP_ID_KEY"

class EditTaskActivity : EditItemActivity() {
    override lateinit var vm : EditTaskViewModel


//    private lateinit var nameEditText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        vm = EditTaskViewModel.create(this)
        super.onCreate(savedInstanceState)
        vm.currentItem.observe(this, Observer { (adapter as EditTaskListAdapter).handleDataChanged(); Log.i("taskList.debug","change observed in currentItem") })
    }

    override fun onEditTypeChange(newType: Int) {
        super.onEditTypeChange(newType)
        when(newType) {
            TYPE_VIEW -> {
                nameTextView.text = (vm.currentItem.value as TaskItem).title
            }
            TYPE_ADD -> {
                if(vm.isCopy) nameEditText.setText(vm.currentItem.value!!.title) else nameEditText.setText("")
            }
            TYPE_UPDATE -> {
                nameEditText.setText((vm.currentItem.value as TaskItem).title)
            }
        }
    }

    override fun validateInputs(): Pair<Boolean, Int> {
        return Pair(!vm.isInvalidText(nameEditText.text.toString()),0)
    }

    override fun makeAdapter() : EditTaskListAdapter {
        return EditTaskListAdapter(this, vm) { openGroupSelector() }
    }

    override fun View.showValidationErrorMessage(type: Int){//TODO: specify
        Snackbar.make(this,"Invalid input", Snackbar.LENGTH_LONG).show()
    }

    override fun doBeforeSave() {
        vm.currentItem.value?.apply{this.title = nameEditText.text.toString()}
    }

    private fun openGroupSelector() {
        SelectGroupDialog(vm::setGroupId).show(supportFragmentManager,"groupDialog")
        Log.d("ETLA","click registered")
    }

//    private fun handleItemDeleted() {
//        vm.delete(vm.currentItem.value!!)
//        finish()
//    }

    override fun handleItemCopied() {
        val intent = Intent(this, EditTaskActivity::class.java).apply{
            putExtra(TYPE_INTENT_KEY, TYPE_ADD)
            putExtra(TASK_ITEM_KEY, vm.currentItem.value)
            putExtra(COPIED_KEY,true)
            try {putParcelableArrayListExtra(GROUPLIST_KEY,ArrayList(vm.groupList))} catch (e: NullPointerException) {}
        }
        this.startActivity(intent)
    }

//    //Return to editType view if back button clicked in editType update
//    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
//        return if (vm.openedAsView && vm.editType.value == TYPE_UPDATE && keyCode == KeyEvent.KEYCODE_BACK){
//            vm.editType.value = TYPE_VIEW
//            true
//        }
//        else super.onKeyUp(keyCode, event)
//    }

//    fun shortToast(text : String) {Toast.makeText(this,text,Toast.LENGTH_SHORT).show()}
}
@SuppressLint("ValidFragment")
class SelectGroupDialog(private val setGroupId : (Long) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val vm = EditTaskViewModel.create(it)
            val titles = mutableListOf<String>("None")
            titles.addAll(vm.groupTitlesList)
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Select a group")
                .setItems(titles.toTypedArray()) { dialog, pos ->
                    if (pos > 0) vm.setGroupId(vm.groupList[pos - 1].id!!)
                    else vm.setGroupId(-1)//TODO last changes
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}