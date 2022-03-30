package com.project.tahlilukclient.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.tahlilukclient.adapters.LabsInfoAdapter
import com.project.tahlilukclient.databinding.ActivityLabsBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.listeners.LabListener
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.SupportFunctions
import kotlinx.coroutines.*
import java.util.*


class LabsActivity : AppCompatActivity(), LabListener {
    lateinit var activityLabsBinding: ActivityLabsBinding
    private var parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + parentJob)
    private lateinit var labsListAdapter: LabsInfoAdapter
    private var labsList: ArrayList<Lab> = ArrayList()
    private lateinit var searchView: SearchView
    var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLabsBinding = ActivityLabsBinding.inflate(LayoutInflater.from(this))
        setContentView(activityLabsBinding.root)
        setListeners()
        searchOnLabs()
    }

    private fun getLabsFromFireStore() {
        FirestoreClass().getLabs(this, Constants.Key_COLLECTION_LABS)
    }

    private fun setListeners() {
        activityLabsBinding.imageBack.setOnClickListener {
            onBackPressed()
        }
        activityLabsBinding.search.setOnSearchClickListener {
            activityLabsBinding.tvTitle.visibility = View.GONE
        }

        activityLabsBinding.rg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                activityLabsBinding.rbMap.id -> {
                    if (labsListAdapter.currentLatLong != null) {
                        activityLabsBinding.userRecyclerView.visibility = View.GONE
                        SupportFunctions.loading(true, null, activityLabsBinding.progressBar)
                        val intent = Intent(applicationContext, MapActivity::class.java)
                        intent.putExtra(
                            Constants.KEY_CURRENT_LATITUDE,
                            labsListAdapter.currentLatLong!!.latitude
                        )
                        intent.putExtra(
                            Constants.KEY_CURRENT_LONGITUDE,
                            labsListAdapter.currentLatLong!!.longitude
                        )
                        startActivity(intent)

                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                            != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            SupportFunctions.getPermission(this)
                        } else if (!SupportFunctions.isGpsEnabled(this)) {
                            SupportFunctions.turnOnGps(this)

                        } else {

                            try {
                                if (count != 3) {
                                    SupportFunctions.showSwitcher(
                                        false,
                                        activityLabsBinding.rg
                                    )
                                    reloadRecyclerView(false)
                                    count += 1
                                    coroutineScope.launch {
                                        delay(2000)
                                        withContext(Dispatchers.Main) {
                                            activityLabsBinding.rbList.performClick()
                                            activityLabsBinding.rbMap.performClick()
                                        }
                                    }
                                } else {
                                    SupportFunctions.showSwitcher(
                                        true,
                                        activityLabsBinding.rg
                                    )
                                    SupportFunctions.showDialog(this, false)
                                    count = 0
                                }
                            } catch (ex: Exception) {
                            }
                        }
                    }
                }
            }
        }
    }

    fun successLabFromFireStore(labList: ArrayList<Lab>) {
        if (labList.size > 0) {
            SupportFunctions.loading(false, null, activityLabsBinding.progressBar)
            labsList = labList
            labsListAdapter = LabsInfoAdapter(this, labList, this, false)
            activityLabsBinding.userRecyclerView.visibility = View.VISIBLE
            activityLabsBinding.userRecyclerView.layoutManager = LinearLayoutManager(this)
            activityLabsBinding.userRecyclerView.adapter = labsListAdapter
        }
    }

    fun closeProgressBar() {
        SupportFunctions.loading(false, null, activityLabsBinding.progressBar)
        activityLabsBinding.textErrorMessage.visibility = View.VISIBLE
    }


    private fun searchOnLabs() {
        searchView = activityLabsBinding.search
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
            activityLabsBinding.tvTitle.visibility = View.VISIBLE
            true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filerList(labName: String?) {
        val filteredLabList: ArrayList<Lab> = ArrayList()
        for (lab in labsList) {
            if (labName != null) {
                if (lab.name?.lowercase(Locale.getDefault())
                        ?.contains(labName.lowercase(Locale.getDefault())) == true
                )
                    filteredLabList.add(lab)
            }
        }
        if (filteredLabList.isNotEmpty()) {
            labsListAdapter.setFilteredList(filteredLabList)
            labsListAdapter.notifyDataSetChanged()
        }
    }

    override fun onLabClicked(lab: Lab) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_LAB, lab)
        startActivity(intent)
    }


    fun reloadRecyclerView(State: Boolean) {
        labsListAdapter = LabsInfoAdapter(this, labsList, this, State)
        activityLabsBinding.userRecyclerView.adapter = labsListAdapter
    }

    fun clickOnMapShow() {
        activityLabsBinding.rbList.performClick()
        activityLabsBinding.rbMap.performClick()
    }

    override fun onResume() {
        super.onResume()
        SupportFunctions.loading(true, null, activityLabsBinding.progressBar)
        getLabsFromFireStore()
        activityLabsBinding.rbList.isChecked = true
    }

    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel()
    }

}