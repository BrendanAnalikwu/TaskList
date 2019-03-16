package cerbrendus.tasklist.Main

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cerbrendus.tasklist.R
import cerbrendus.tasklist.dataClasses.TaskItem


const val ARG_GROUPID = "cerbrendus.tasklist.groupid"
class ListFragment : Fragment() {
    private var groupId: Long = -1
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf {it.containsKey(ARG_GROUPID)}?.apply{
            groupId = this.getLong(ARG_GROUPID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView : ViewGroup = inflater.inflate(R.layout.fragment_list, container, false) as ViewGroup
        //get ViewModel
        val vm = MainActivityViewModel.create(activity!!)
        val itemList = when (groupId) {
            (-1).toLong() -> vm.allItems
            else -> vm.getAllItemsInGroup(groupId)
        }

        //Get RecyclerView handle
        val recyclerView: RecyclerView = rootView.findViewById<RecyclerView>(R.id.main_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        val itemDecor = DividerItemDecoration(context, (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(itemDecor)

        //Set RecyclerView adapters
        val adapter = ItemAdapter(itemList.value.orEmpty(), activity!!)
        recyclerView.adapter = adapter

        //update list content
        itemList.observe(this, Observer<List<TaskItem>> { newList: List<TaskItem> -> adapter.setItems(newList)
            Log.d("check","opgelost?$groupId")
        })
        vm.allCheckedItems.observe(this, Observer { Log.d("check","opgelost?$groupId")})

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
