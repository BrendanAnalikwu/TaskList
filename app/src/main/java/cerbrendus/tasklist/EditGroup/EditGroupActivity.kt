package cerbrendus.tasklist.EditGroup

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import cerbrendus.tasklist.BaseClasses.*
import cerbrendus.tasklist.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateGroupActivity : EditActivity() {

    override lateinit var vm : EditGroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        vm = EditGroupViewModel.create(this)
        super.onCreate(savedInstanceState)
        vm.currentGroup.observe(this, Observer { adapter.handleDataChanged()})
        adapter.handleDataChanged()
    }

    override fun onEditTypeChange(newType: Int) {
        super.onEditTypeChange(newType)
        when(newType) {
            TYPE_VIEW -> {
                nameTextView.text = vm.currentGroup.value!!.title
            }
            TYPE_ADD -> {
                if(vm.isCopy) nameEditText.setText(vm.currentGroup.value!!.title) else nameEditText.setText("")
            }
            TYPE_UPDATE -> {
                nameEditText.setText(vm.currentGroup.value!!.title)
            }
        }
    }

    override fun validateInputs(): Pair<Boolean, Int> {
        val text = nameEditText.text.toString()
        return Pair(!vm.isInvallidText(text),0)
    }

    override fun makeAdapter(): EditAdapter {
        return EditGroupAdapter(this)
    }

    override fun View.showValidationErrorMessage(type: Int){//TODO: specify
        Snackbar.make(this,context.getString(R.string.invalid_input), Snackbar.LENGTH_LONG).show()
    }

    override suspend fun doBeforeFinish(): Boolean {
        vm.currentGroup.postValue(vm.currentGroup.value?.apply { title = nameEditText.text.toString() })
        vm.save()
        return true
    }

    override suspend fun handleDeleted() {
        DeleteGroupDialog {checked ->
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch{
                if (checked) vm.deleteItemsInGroup(vm.currentGroup.value!!)
                super.handleDeleted()
            }
            finish()
        }.show(supportFragmentManager,"delete_group_dialog")
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
