package cerbrendus.tasklist.Main

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem


const val ARG_GROUPID = "cerbrendus.tasklist.groupid"
class ListFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var vm: ListFragmentViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf {it.containsKey(ARG_GROUPID)}?.apply{
            ListFragmentViewModel.create(this@ListFragment).configure(_groupId = this.getLong(ARG_GROUPID))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView : ViewGroup = inflater.inflate(R.layout.fragment_list, container, false) as ViewGroup
        //get ViewModel
        vm = ListFragmentViewModel.create(this)

        //Get RecyclerView handle
        recyclerView = rootView.findViewById<RecyclerView>(R.id.main_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        val itemDecor = DividerItemDecoration(context, (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(itemDecor)

        //Set RecyclerView adapter
        val adapter = ItemAdapter(vm.itemList.value.orEmpty(), this)
        recyclerView.adapter = adapter

        // Set drag handler
        val dragHandler = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN ,0) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                //vm.moveItem(viewHolder.adapterPosition,target.adapterPosition)
                vm.onItemMoved(adapter,viewHolder.adapterPosition,target.adapterPosition)
                return true
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                vm.savePriority()
                Log.i("tasklist.debug","clearView was called")
            }
        }
        val dragHelper = ItemTouchHelper(dragHandler)
        dragHelper.attachToRecyclerView(recyclerView)

        //update list content
        vm.itemList.observe(this, Observer<List<TaskItem>>{ newList: List<TaskItem> -> vm.itemListChange(newList,adapter)})
        vm.aVM.allCheckedItems.observe(this, Observer { Log.i("check","opgelost?${vm.groupId}")})

        vm.aVM.allItems.observe(this, Observer { newList: List<TaskItem> -> vm.movedAllItemList = newList.toMutableList()})

        return rootView
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            //throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(groupId: Long) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_GROUPID,groupId)
                }
            }
    }
}
