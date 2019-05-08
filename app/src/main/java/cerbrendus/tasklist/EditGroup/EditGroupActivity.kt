package cerbrendus.tasklist.EditGroup

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cerbrendus.tasklist.BaseClasses.EditActivity
import cerbrendus.tasklist.BaseClasses.TYPE_ADD
import cerbrendus.tasklist.BaseClasses.TYPE_UPDATE
import cerbrendus.tasklist.BaseClasses.TYPE_VIEW
import cerbrendus.tasklist.R
import com.google.android.material.snackbar.Snackbar

class CreateGroupActivity : EditActivity() {

    override val vm = EditGroupViewModel.create(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onEditTypeChange(newType: Int) {
        super.onEditTypeChange(newType)
        when(newType) {
            TYPE_VIEW -> {
                nameTextView.text = vm.currentGroup.value!!.title
            }
            TYPE_ADD -> {
            }
            TYPE_UPDATE -> {
                nameEditText.setText(vm.currentGroup.value!!.title)
            }
        }
    }

    override fun validateInputs(): Pair<Boolean, Int> {
        val text = nameEditText.text.toString()
        return Pair(vm.isInvallidText(text),0)
    }

    override fun View.showValidationErrorMessage(type: Int){//TODO: specify
        Snackbar.make(this,"Invalid input", Snackbar.LENGTH_LONG).show()
    }

    override fun doBeforeSave() {
        vm.currentGroup.value?.apply { this.title = nameEditText.text.toString() }
    }

    override fun handleDeleted() {
        DeleteGroupDialog {checked ->
            if (checked) vm.deleteItemsInGroup(vm.currentGroup.value!!)
            super.handleDeleted()
            finish()
        }.show(supportFragmentManager,"delete_group_dialog")
    }

    //Return to editType view if back button clicked in editType update
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        val vm = EditGroupViewModel.create(this)
        return if (vm.openedAsView && vm.editType.value == TYPE_UPDATE && keyCode == KeyEvent.KEYCODE_BACK){
            vm.editType.value = TYPE_VIEW
            true
        }
        else super.onKeyUp(keyCode, event)
    }

}

@SuppressLint("ValidFragment")
class DeleteGroupDialog(private val confirm : (Boolean) -> Unit ) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val checkView = activity!!.layoutInflater.inflate(R.layout.delete_dialog_view,null)

            builder.setTitle("Are you sure?")
                .setMessage("Deleting a group cannot be undone")
                .setPositiveButton("OK") { _,_ -> confirm(try{checkView.findViewById<CheckBox>(R.id.delete_dialog_checkbox).isChecked} catch(e: Exception) {false}) }
                .setNegativeButton("Cancel") {_,_->}
                .setView(checkView)


            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
