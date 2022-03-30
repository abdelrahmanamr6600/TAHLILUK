package com.project.tahlilukclient.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.tahlilukclient.adapters.LabsAdapter
import com.project.tahlilukclient.databinding.ActivityLabsChatBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.listeners.LabListener
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.SupportFunctions
import java.util.*
import kotlin.collections.ArrayList

class LabsChatActivity : BaseActivity(), LabListener {
    lateinit var activityLabsChatBinding: ActivityLabsChatBinding
    private lateinit var labsList: ArrayList<Lab>
    private var filteredLabList: ArrayList<Lab> = ArrayList()
    private lateinit var searchView: SearchView
    private lateinit var labsListAdapter: LabsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLabsChatBinding = ActivityLabsChatBinding.inflate(layoutInflater)
        setContentView(activityLabsChatBinding.root)
        setListeners()
        getLabsFromFireStore()
        searchOnLabs()
    }

    private fun setListeners() {
        activityLabsChatBinding.imageBack.setOnClickListener {
            onBackPressed()
        }

        activityLabsChatBinding.search.setOnSearchClickListener {
            activityLabsChatBinding.tvTitle.visibility = View.GONE
        }

    }

    private fun getLabsFromFireStore() {
        FirestoreClass().getLabs(this,Constants.Key_COLLECTION_LABS)
    }

    fun successLabFromFireStore(labList: ArrayList<Lab>) {
        if (labList.size > 0) {
            labsList = ArrayList()
            labsList = labList
            activityLabsChatBinding.userRecyclerView.visibility = View.VISIBLE
            SupportFunctions.loading(false, null, activityLabsChatBinding.progressBar)
            labsListAdapter = LabsAdapter(labList, this)
            activityLabsChatBinding.userRecyclerView.layoutManager = LinearLayoutManager(this)
            activityLabsChatBinding.userRecyclerView.adapter = labsListAdapter
        } else {
            showErrorMessage()
        }
    }


    private fun showErrorMessage() {
        activityLabsChatBinding.textErrorMessage.text = String.format("%s", "No user available")
        activityLabsChatBinding.textErrorMessage.visibility = View.VISIBLE
    }


    override fun onLabClicked(lab: Lab) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_LAB, lab)
        startActivity(intent)
        finish()
    }

    fun closeProgressBar() {
        SupportFunctions.loading(false, null, activityLabsChatBinding.progressBar)
    }

    private fun searchOnLabs() {
        searchView = activityLabsChatBinding.search

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
            activityLabsChatBinding.tvTitle.visibility = View.VISIBLE
            true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filerList(labName: String?) {
        filteredLabList = java.util.ArrayList()
        for (lab in labsList) {
            if (labName != null) {
                if (lab.name?.lowercase(Locale.getDefault())
                        ?.contains(labName.lowercase(Locale.getDefault())) == true
                )
                    filteredLabList.add(lab)
            }
        }
        if (filteredLabList.isNotEmpty()) {
            val adapter = LabsAdapter()
            labsListAdapter.setFilteredList(filteredLabList)
            adapter.notifyDataSetChanged()
        }
    }
}