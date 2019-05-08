package cerbrendus.tasklist.BaseClasses

import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import cerbrendus.tasklist.R
import com.google.android.material.snackbar.Snackbar

abstract class EditActivity : AppCompatActivity() {
    abstract val vm : EditViewModel
    lateinit var nameTextView : TextView
    lateinit var nameEditText : EditText
    lateinit var saveButton : Button
    lateinit var menuButton : ImageButton
    lateinit var updateButton : ImageButton
    lateinit var exitButton : ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        // Pass intent to ViewModel
        if(!vm.configure(intent)) finish() // finish when configuration fails

        //Set handles
        nameTextView = findViewById(R.id.acg_textview_name)
        nameEditText = findViewById(R.id.acg_edittext_name)
        saveButton = findViewById(R.id.acg_button_save)
        menuButton = findViewById(R.id.acg_button_menu)
        updateButton = findViewById(R.id.acg_button_update)
        exitButton = findViewById(R.id.acg_button_exit)

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
                        handleDeleted()
                        true
                    }
                    else -> false
                }
            }
        }

        //Setup Save Button and handle validation
        saveButton.setOnClickListener {
            val validation = validateInputs()
            if (!validation.first) it.showValidationErrorMessage(validation.second)
            else {
                doBeforeSave()
                if (vm.save()) finish()
            }
        }

        //Set view visibilities when editType changed
        vm.editType.observe(this, Observer{newType -> onEditTypeChange(newType)})

    }

    open fun onEditTypeChange(newType: Int) {
        when(newType){
            TYPE_VIEW -> {
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
                saveButton.visibility  = View.VISIBLE
                menuButton.visibility = View.INVISIBLE
                updateButton.visibility = View.INVISIBLE
            }
        }
    }

    /** Override this to perform validation, returning a pair containing a success value and an int to specify the type
     * of error.
     */
    abstract fun validateInputs() : Pair<Boolean,Int>

    /** Override this to perform some actions such as updating the current item*/
    open fun doBeforeSave() {}

    /** Override this to customize the error message based on the validation */
    open fun View.showValidationErrorMessage(type: Int) {
        Snackbar.make(this,"Invalid input", Snackbar.LENGTH_LONG).show()
    }

    /** Lets the VM handle the deletion*/
    open fun handleDeleted() {
        vm.handleDeleted()
    }

    /** Lets the VM handle the deletion*/
    open fun handleAdded() {
        vm.handleAdded()
    }

    /** Lets the VM handle the deletion*/
    open fun handleUpdated() {
        vm.handleUpdated()
    }

    fun shortToast(text : String) {
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
}