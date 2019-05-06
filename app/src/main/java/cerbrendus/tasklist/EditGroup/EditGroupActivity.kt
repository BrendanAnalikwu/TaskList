package cerbrendus.tasklist.EditGroup

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import cerbrendus.tasklist.EditTaskItem.TYPE_ADD
import cerbrendus.tasklist.EditTaskItem.TYPE_UPDATE
import cerbrendus.tasklist.EditTaskItem.TYPE_VIEW
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.Group
import com.google.android.material.snackbar.Snackbar

//Defined in EditTaskActivity
//const val TYPE_INTENT_KEY = "cerbrendus.tasklist.EditTaskItem.TYPE_INTENT_KEY"
//const val TYPE_ADD = 0
//const val TYPE_UPDATE = 1
//const val TYPE_VIEW = 2
const val GROUP_KEY = "cerbrendus.tasklist.EditGroup.GROUP_KEY"
const val ITEM_LIST_KEY = "cerbrendus.tasklist.EditGroup.ITEM_LIST_KEY"

class CreateGroupActivity : AppCompatActivity() {
    private var group : Group? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        //Get viewModel
        val vm = EditGroupViewModel.create(this)

        // Pass intent to ViewModel
        if(!vm.configure(intent)) finish() // finish when configuration fails

        //Set handles
        val nameTextView = findViewById<TextView>(R.id.acg_textview_name)
        val nameEditText = findViewById<EditText>(R.id.acg_edittext_name)
        val saveButton = findViewById<Button>(R.id.acg_button_save)
        val menuButton = findViewById<ImageButton>(R.id.acg_button_menu)
        val updateButton = findViewById<ImageButton>(R.id.acg_button_update)
        val exitButton = findViewById<ImageButton>(R.id.acg_button_exit)


        //Setup exit button
        exitButton.setOnClickListener{ finish() }

        //Setup update button
        updateButton.setOnClickListener { vm.editType.value = TYPE_UPDATE }

        //Setup menu button, inflate menu and handle menuItem clicks
        menuButton.setOnClickListener { it ->
            val popup = PopupMenu(this,it)
            val menuInflater : MenuInflater = popup.menuInflater
            menuInflater.inflate(R.menu.create_group_menu, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.delete_group_item -> {
                        handleGroupDeleted()
                        Log.d("action","delete item")
                        true
                    }
                    else -> false
                }
            }
        }

        //Setup Save Button and handle validation
        saveButton.setOnClickListener {
            val text = nameEditText.text.toString()
            if (vm.isInvallidText(text)) Snackbar.make(it,"Please add a name", Snackbar.LENGTH_LONG).show()
            else {
                vm.currentGroup.value?.apply { this.title = text }
                if (vm.save()) finish()
            }
        }

        //Set view visibilities when editType changed
        vm.editType.observe(this, Observer{
            when(it){
                TYPE_VIEW -> {
                    nameTextView.text = vm.currentGroup.value!!.title
                    nameTextView.visibility = View.VISIBLE
                    nameEditText.visibility = View.INVISIBLE
                    saveButton.visibility  = View.INVISIBLE
                    menuButton.visibility = View.VISIBLE
                    updateButton.visibility = View.VISIBLE
                }
                TYPE_ADD -> {
                    nameTextView.visibility = View.INVISIBLE
                    nameEditText.visibility = View.VISIBLE
                    nameEditText.setText("")
                    saveButton.visibility  = View.VISIBLE
                    menuButton.visibility = View.INVISIBLE
                    updateButton.visibility = View.INVISIBLE
                }
                TYPE_UPDATE -> {
                    nameTextView.visibility = View.INVISIBLE
                    nameEditText.visibility = View.VISIBLE
                    nameEditText.setText(vm.currentGroup.value!!.title)
                    saveButton.visibility  = View.VISIBLE
                    menuButton.visibility = View.INVISIBLE
                    updateButton.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun handleGroupDeleted() {
        DeleteGroupDialog {checked ->
            val vm = EditGroupViewModel.create(this)
            if (checked) vm.deleteItemsInGroup(vm.currentGroup.value!!)
            vm.deleteGroup(vm.currentGroup.value!!)
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
            val vm = EditGroupViewModel.create(it)
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
