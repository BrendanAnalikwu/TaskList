package cerbrendus.tasklist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuInflater
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.Adapters.EditTaskListAdapter
import cerbrendus.tasklist.ViewModels.EditViewModel
import cerbrendus.tasklist.dataClasses.Group
import cerbrendus.tasklist.dataClasses.TaskItem
import com.google.android.material.snackbar.Snackbar

const val TYPE_INTENT_KEY = "cerbrendus.tasklist.TYPE_INTENT_KEY"
const val TYPE_ADD = 0
const val TYPE_UPDATE = 1
const val TYPE_VIEW = 2
const val TASK_ITEM_KEY = "cerbrendus.tasklist.TASK_ITEM_KEY"
const val GROUPLIST_KEY = "cerbrendus.tasklist.GROUPLIST_KEY"

class EditTaskActivity : AppCompatActivity() {
    private lateinit var vm : EditViewModel


    private lateinit var nameEditText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        vm = EditViewModel.create(this)

        // Pass intent to ViewModel
        vm.intent = intent
        if (!vm.configure()) finish() // finish when configuration fails //TODO: implement error handling

        //Setup attribute recyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.edit_task_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val adapter = EditTaskListAdapter(this) {openGroupSelector()}
        recyclerView.adapter = adapter
        adapter.setGroupTitleSetup { tv ->
            if (tv==null) shortToast("Niet gevonden") //TODO: remove for release
            tv?.text = vm.getGroupFromId(vm.currentItem.value!!.group_id)?.title ?: "No group selected"
            vm.currentItem.observe(this, Observer {tv?.text = vm.getGroupFromId(it.group_id)?.title ?: "No group selected" })
            true
        }

        //Setup exit button
        val exitButton = findViewById<ImageButton>(R.id.ant_button_exit)
        exitButton.setOnClickListener{ finish() }

        //Setup update button
        val updateButton = findViewById<ImageButton>(R.id.ant_button_update)
        updateButton.setOnClickListener { vm.editType.value = TYPE_UPDATE }

        //Setup menu button, inflate menu and handle menuItem clicks
        val menuButton = findViewById<ImageButton>(R.id.ant_button_menu)
        menuButton.setOnClickListener { it ->
            val popup = PopupMenu(this,it)
            val menuInflater : MenuInflater = popup.menuInflater
            menuInflater.inflate(R.menu.edit_item_activity_menu, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.delete_item -> {
                        handleItemDeleted()
                        Log.d("action","delete item")
                        true
                    }
                    R.id.copy_item -> {
                        //TODO: implement copy option
                        Log.d("action","copy item")
                        true
                    }
                    else -> false
                }
            }
        }

        //TODO("correct icons")
        //TODO("icon margins")
        //TODO("icon size")

        //Get handles for EditText and TextView
        nameEditText = findViewById<EditText>(R.id.ant_edittext_name)
        val nameTextView = findViewById<TextView>(R.id.ant_textview_name)

        //Setup Save Button and handle validation
        val saveButton = findViewById<Button>(R.id.ant_button_save)
        saveButton.setOnClickListener {
            val text = nameEditText.text.toString()
            if(vm.isInvalidText(text)) Snackbar.make(it,"Please add a description",Snackbar.LENGTH_LONG).show()
            else {
                vm.currentItem.value?.apply{this.title = text}

                if (vm.save()) finish()
            }
        }

        //Set view visibilities when editType changed
        vm.editType.observe(this, Observer{
            when(it){
                TYPE_VIEW -> {
                    nameTextView.text = (vm.currentItem.value as TaskItem).title
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
                    nameEditText.setText((vm.currentItem.value as TaskItem).title)
                    saveButton.visibility  = View.VISIBLE
                    menuButton.visibility = View.INVISIBLE
                    updateButton.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun openGroupSelector() {
        SelectGroupDialog(::setGroupId).show(supportFragmentManager,"groupDialog")
        Log.d("ETLA","click registered")
    }

    private fun setGroupId(selectedGroupId : Long) {
        vm.currentItem.value?.group_id = selectedGroupId
    }

    private fun handleItemDeleted() {
        vm.delete(vm.currentItem.value!!)
        finish()
    }

    //Return to editType view if back button clicked in editType update
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return if (vm.ETAOpenedAsView && vm.editType.value == TYPE_UPDATE && keyCode == KeyEvent.KEYCODE_BACK){
            vm.editType.value = TYPE_VIEW
            true
        }
        else super.onKeyUp(keyCode, event)
    }

    fun shortToast(text : String) {Toast.makeText(this,text,Toast.LENGTH_SHORT).show()}
}
@SuppressLint("ValidFragment")
class SelectGroupDialog(private val setGroupId : (Long) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val vm = EditViewModel.create(it)
            val titles = mutableListOf<String>("None")
            titles.addAll(vm.groupTitlesList)
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Select a group")
                .setItems(titles.toTypedArray()) { dialog, pos ->
                    if (pos > 0) setGroupId(vm.groupList[pos - 1].id!!)
                    else setGroupId(-1)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}