package cerbrendus.tasklist.BaseClasses

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.EditGroup.EditGroupViewModel
import cerbrendus.tasklist.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class EditActivity : AppCompatActivity() {
    abstract val vm: EditViewModel
    lateinit var nameTextView: TextView
    lateinit var nameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var menuButton: ImageButton
    private lateinit var updateButton: ImageButton
    private lateinit var exitButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: EditAdapter
    private val scope = CoroutineScope(Dispatchers.Default)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Pass intent to ViewModel
        if (!vm.configure(intent)) finish() // finish when configuration fails

        //Set handles
        nameTextView = findViewById(R.id.edit_textview_name)
        nameEditText = findViewById(R.id.edit_edittext_name)
        saveButton = findViewById(R.id.edit_button_save)
        menuButton = findViewById(R.id.edit_button_menu)
        updateButton = findViewById(R.id.edit_button_update)
        exitButton = findViewById(R.id.edit_button_exit)
        recyclerView = findViewById(R.id.edit_recyclerview)

        //Setup exit button
        exitButton.setOnClickListener { finish() }

        //Setup update button
        updateButton.setOnClickListener { vm.editType.value = TYPE_UPDATE }

        //Setup menu button, inflate menu and handle menuItem clicks
        setupMenu()

        //Setup Save Button and handle validation
        saveButton.setOnClickListener {
            val validation = validateInputs()
            if (!validation.first) it.showValidationErrorMessage(validation.second)
            else {
                scope.launch {
                    if (doBeforeFinish()) {
                        if (vm.openedAsView) vm.editType.postValue(TYPE_VIEW)
                        else finish()
                    } else Log.i("qwerty", "aserty")
                }
            }
        }

        //Setup recyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        adapter = makeAdapter()
        recyclerView.adapter = adapter
        val itemDecor = DividerItemDecoration(this, (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(itemDecor)


        //Set view visibilities when editType changed
        vm.editType.observe(
            this,
            Observer { newType -> onEditTypeChange(newType);Log.i("tasklist.debug", "editType changed") })

    }

    private fun setupMenu() {
        menuButton.setOnClickListener { it ->
            val popup = PopupMenu(this, it)
            val menuInflater: MenuInflater = popup.menuInflater
            menuInflater.inflate(menuReference, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_group_item -> {
                        handleDeleted()
                        true
                    }
                    else -> customSetupMenu(it.itemId)
                }
            }
        }
        //TODO("correct icons")
        //TODO("icon margins")
        //TODO("icon size")
    }

    /**
     * Add custom cases in the setOnMenuItemClickListener in [setupMenu]
     * When implemented, should contain something like:
     * ```
     * when (id) {
     *     R.id.menu_item -> {
     *         doSomething()
     *         true
     *     }
     *     else -> false
     * ```
     * @param id id of the menu item
     */
    open fun customSetupMenu(id : Int) : Boolean = false

    /**
     * Reference to the menu resource
     */
    open val menuReference = R.menu.edit_activity_menu

    abstract fun makeAdapter(): EditAdapter

    open fun onEditTypeChange(newType: Int) {
        when (newType) {
            TYPE_VIEW -> {
                nameTextView.visibility = View.VISIBLE
                nameEditText.visibility = View.INVISIBLE
                saveButton.visibility = View.INVISIBLE
                menuButton.visibility = View.VISIBLE
                updateButton.visibility = View.VISIBLE
            }
            TYPE_ADD -> {
                nameTextView.visibility = View.INVISIBLE
                nameEditText.visibility = View.VISIBLE
                nameEditText.setText("")
                saveButton.visibility = View.VISIBLE
                menuButton.visibility = View.INVISIBLE
                updateButton.visibility = View.INVISIBLE
            }
            TYPE_UPDATE -> {
                nameTextView.visibility = View.INVISIBLE
                nameEditText.visibility = View.VISIBLE
                saveButton.visibility = View.VISIBLE
                menuButton.visibility = View.INVISIBLE
                updateButton.visibility = View.INVISIBLE
            }
        }
    }

    /** Override this to perform validation, returning a pair containing a success value and an int to specify the type
     * of error.
     */
    abstract fun validateInputs(): Pair<Boolean, Int>

    /** Override this to perform some actions such as updating the current item*/
    open suspend fun doBeforeFinish() = true

    /** Override this to customize the error message based on the validation */
    open fun View.showValidationErrorMessage(type: Int) {
        Snackbar.make(this, context.getString(R.string.invalid_input), Snackbar.LENGTH_LONG).show()
    }

    /** Lets the VM handle the deletion*/
    open fun handleDeleted() {
        scope.launch { vm.handleDeleted() }
        finish()
    }

    //Return to editType view if back button clicked in editType update
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        val vm = EditGroupViewModel.create(this)
        return if (vm.openedAsView && vm.editType.value == TYPE_UPDATE && keyCode == KeyEvent.KEYCODE_BACK) {
            vm.editType.value = TYPE_VIEW
            true
        } else super.onKeyUp(keyCode, event)
    }
}

const val TASK_ITEM_REQUEST = 0