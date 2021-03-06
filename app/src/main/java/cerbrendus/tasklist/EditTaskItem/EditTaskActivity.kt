package cerbrendus.tasklist.EditTaskItem

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import cerbrendus.tasklist.BaseClasses.*
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

const val TASK_ITEM_KEY = "cerbrendus.tasklist.Edit.TASK_ITEM_KEY"
const val CURRENT_GROUP_ID_KEY = "cerbrendus.tasklist.Edit.CURRENT_GROUP_ID_KEY"
const val IS_SUBLIST_ITEM_KEY = "cerbrendus.tasklist.Edit.IS_SUBLIST_ITEM_KEY"

class EditTaskActivity : EditActivity() {
    override lateinit var vm: EditTaskViewModel


//    private lateinit var nameEditText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        vm = EditTaskViewModel.create(this)
        super.onCreate(savedInstanceState)
        vm.currentItem.observe(this, Observer { newItem ->
            adapter.handleDataChanged()
            vm.sublist = vm.itemRepo.getItemsById(vm.currentItem.value?.getSublistAsList().orEmpty())
            vm.sublist.observe(
                this,
                Observer { (adapter as EditTaskListAdapter).handleSublistChanged() })
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        vm.handleResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onEditTypeChange(newType: Int) {
        super.onEditTypeChange(newType)
        when (newType) {
            TYPE_VIEW -> {
                nameTextView.text = (vm.currentItem.value as TaskItem).title
            }
            TYPE_ADD -> {
                if (vm.isCopy) nameEditText.setText(vm.currentItem.value!!.title) else nameEditText.setText("")
            }
            TYPE_UPDATE -> {
                nameEditText.setText((vm.currentItem.value as TaskItem).title)
            }
        }
    }

    override fun validateInputs(): Pair<Boolean, Int> {
        return Pair(!vm.isInvalidText(nameEditText.text.toString()), 0)
    }

    override fun makeAdapter(): EditTaskListAdapter {
        return EditTaskListAdapter(this, vm) { openGroupSelector() }
    }

    override fun View.showValidationErrorMessage(type: Int) {//TODO: specify
        Snackbar.make(this, "Invalid input", Snackbar.LENGTH_LONG).show()
    }

    override suspend fun doBeforeFinish(): Boolean {
        vm.currentItem.postValue(vm.currentItem.value?.apply { title = nameEditText.text.toString() })

        // save the item and set the id as the result
        val resultId = vm.save()
        if (resultId != null) {
            setResult(Activity.RESULT_OK, Intent().putExtra(TASK_ITEM_KEY, resultId))
        }
        return true
    }

    private fun openGroupSelector() {
        SelectGroupDialog().show(supportFragmentManager, "groupDialog")
        Log.d("ETLA", "click registered")
    }

//TODO: ask to delete subitems when deleted (if it has subitems)

    private fun handleItemCopied() {
        val intent = Intent(this, EditTaskActivity::class.java).apply {
            putExtra(TYPE_INTENT_KEY, TYPE_ADD)
            putExtra(TASK_ITEM_KEY, vm.currentItem.value)
            putExtra(COPIED_KEY, true)
            try {
                putParcelableArrayListExtra(GROUPLIST_KEY, ArrayList(vm.groupList))
            } catch (e: NullPointerException) {
            }
        }
        this.startActivity(intent)
    }

    /**
     * Open an instance of EditTaskActivity
     */
    fun openEditTaskActivity(type: Int, group_id: Long? = null, isSublistItem: Boolean = false) {
        val intent = Intent(this, EditTaskActivity::class.java)
            .putExtra(TYPE_INTENT_KEY, type)
            .putParcelableArrayListExtra(GROUPLIST_KEY, ArrayList(vm.groupList))
            .putExtra(IS_SUBLIST_ITEM_KEY,isSublistItem)
        if(type == TYPE_ADD) intent.putExtra(CURRENT_GROUP_ID_KEY, group_id)
        scope.launch {
            if (doBeforeFinish() && vm.editType.value == TYPE_ADD) vm.editType.postValue(TYPE_VIEW)
        }
        startActivityForResult(intent, TASK_ITEM_REQUEST)
    }

    override fun customSetupMenu(id: Int): Boolean =
        when (id) {
            R.id.copy_item -> {
                handleItemCopied()
                true
            }
            else -> false
        }

    override val menuReference = R.menu.edit_item_activity_menu

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
class SelectGroupDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val vm = EditTaskViewModel.create(it)
            val titles = mutableListOf("None")
            titles.addAll(vm.groupTitlesList)
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.select_group))
                .setItems(titles.toTypedArray()) { dialog, pos ->
                    if (pos > 0) vm.setGroupId(vm.groupList[pos - 1].id!!)
                    else vm.setGroupId(-1)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}