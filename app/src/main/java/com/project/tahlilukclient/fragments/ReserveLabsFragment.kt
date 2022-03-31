package com.project.tahlilukclient.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.tahlilukclient.R
import com.project.tahlilukclient.adapters.ReserveLabsAdapter
import com.project.tahlilukclient.databinding.FragmentReserveLabsBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.listeners.ChangeStepView
import com.project.tahlilukclient.listeners.LabListener
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.SupportFunctions
import java.util.*


class ReserveLabsFragment : Fragment(),LabListener {

     private lateinit var reserveLabsBinding: FragmentReserveLabsBinding
     private lateinit var labsAdapter:ReserveLabsAdapter
    private var filteredLabList: ArrayList<Lab> = ArrayList()
    private lateinit var labsList: ArrayList<Lab>
    private lateinit var searchView: SearchView
    private lateinit var changeStepView: ChangeStepView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        getLabsFromFireStore()
        reserveLabsBinding = FragmentReserveLabsBinding.inflate(layoutInflater)
        return reserveLabsBinding.root

    }

    override fun onStart() {
        super.onStart()
        searchOnLabs()

    }

    companion object {

        fun newInstance(listener: ChangeStepView) =
            ReserveLabsFragment().apply {
                arguments = Bundle().apply {
                }
                changeStepView=listener
            }
    }



    private fun searchOnLabs(){
         searchView = reserveLabsBinding.searchView


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                filerList(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filerList(newText)
                return true
            }
        })
        searchView.setOnCloseListener {
            searchView.onActionViewCollapsed()
            true
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun filerList(labName: String?) {
        filteredLabList = ArrayList()
        for(lab in labsList){
            if (labName != null) {
                if (lab.name?.lowercase(Locale.getDefault())?.contains(labName.lowercase(Locale.getDefault())) == true)
                    filteredLabList.add(lab)
            }
        }
        if (filteredLabList.isNotEmpty()){
            labsAdapter.setFilteredList(filteredLabList)
            labsAdapter.notifyDataSetChanged()
        }
    }

    private fun getLabsFromFireStore(){
        FirestoreClass().getLabs(this)
    }

    fun successLabFromFireStore(labsList: ArrayList<Lab>) {
        if (labsList.size>0){
            this.labsList = ArrayList()
            this.labsList = labsList

            val layoutManager = LinearLayoutManager(this.activity)
            closeProgressBar()
            reserveLabsBinding.rvLabs.visibility = View.VISIBLE
            labsAdapter  = ReserveLabsAdapter(labsList,this)
         reserveLabsBinding.rvLabs.layoutManager = layoutManager
            reserveLabsBinding.rvLabs.adapter = labsAdapter
        }
    }

    fun closeProgressBar(){
        SupportFunctions.loading(false, null, reserveLabsBinding.progressBar)
    }

    override fun onLabClicked(lab: Lab) {
        changeStepView.changePosition()

        val requestReserveAnalyticsFragment =RequestReserveAnalyticsFragment()
        val bundle = Bundle()
        bundle.putSerializable("lab",lab)
        requestReserveAnalyticsFragment.arguments = bundle
        val fragmentManager: FragmentManager =
            (reserveLabsBinding.root.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, requestReserveAnalyticsFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }
}