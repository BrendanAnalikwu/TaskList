package cerbrendus.tasklist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import cerbrendus.tasklist.ViewModels.GroupViewModel
import cerbrendus.tasklist.dataClasses.Group
import com.google.android.material.snackbar.Snackbar

//Defined in EditTaskActivity
//const val TYPE_INTENT_KEY = "cerbrendus.tasklist.TYPE_INTENT_KEY"
//const val TYPE_ADD = 0
//const val TYPE_UPDATE = 1
//const val TYPE_VIEW = 2
const val GROUP_KEY = "cerbrendus.tasklist.GROUP_KEY"

class CreateGroupActivity : AppCompatActivity() {
    private var group : Group? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        //Get viewModel
        val vm = GroupViewModel.create(this)

        //Get Intent
        vm.editType.value = intent.getIntExtra(TYPE_INTENT_KEY, TYPE_ADD)
        group = intent.getParcelableExtra<Group>(GROUP_KEY)
        //Check that taskItem is set if view or update is type
        if (vm.editType.value != TYPE_ADD && group == null) {
            Log.d("CreateGroupActivity","Geen group voor type!=add")
            finish()
        }

        //Set if opened in view mode
        vm.openedAsView = (vm.editType.value == TYPE_VIEW)


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
            when(text) {
                "" -> Snackbar.make(it,"Please add a name", Snackbar.LENGTH_LONG).show()
                else -> {
                    when(vm.editType.value){
                        TYPE_ADD -> handleItemAdded()
                        TYPE_UPDATE -> handleItemUpdated()
                    }
                }
            }
        }

        //Set view visibilities when editType changed
        vm.editType.observe(this, Observer{
            when(it){
                TYPE_VIEW -> {
                    nameTextView.text = (group as Group).title
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
                    nameEditText.setText((group as Group).title)
                    saveButton.visibility  = View.VISIBLE
                    menuButton.visibility = View.INVISIBLE
                    updateButton.visibility = View.INVISIBLE
                }
            }
        })
    }

    //Handle different  edit actions (update, add, delete)
    private fun handleItemUpdated() {
        val vm = GroupViewModel.create(this)
        group!!.title = findViewById<EditText>(R.id.acg_edittext_name).text.toString()
        vm.updateGroup(group!!)
        vm.editType.value = TYPE_VIEW
    }

    private fun handleItemAdded() {
        val vm = GroupViewModel.create(this)
        val title = findViewById<EditText>(R.id.acg_edittext_name).text.toString()
        vm.createGroup(Group(title=title))
        finish()
    }

    private fun handleGroupDeleted() {
        GroupViewModel.create(this).deleteGroup(group!!)
        finish()
    }
/*
    //Return to editType view if back button clicked in editType update
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return if (vm.ETAOpenedAsView && vm.editType.value == TYPE_UPDATE && keyCode == KeyEvent.KEYCODE_BACK){
            vm.editType.value = TYPE_VIEW
            true
        }
        else super.onKeyUp(keyCode, event)
    }
*/
}
