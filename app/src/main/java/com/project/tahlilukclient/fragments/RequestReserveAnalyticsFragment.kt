package com.project.tahlilukclient.fragments
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.tahlilukclient.adapters.AnalyticsAdapter
import com.project.tahlilukclient.databinding.FragmentAnalyticsRequestReserveBinding
import com.project.tahlilukclient.listeners.AnalyticsListener
import com.project.tahlilukclient.listeners.ChangeStepView
import com.project.tahlilukclient.models.Analytics
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.SupportFunctions
import java.util.*
import kotlin.collections.ArrayList


class RequestReserveAnalyticsFragment : Fragment(),AnalyticsListener {
    private lateinit var binding : FragmentAnalyticsRequestReserveBinding
    private lateinit var bundle: Bundle
    private lateinit var searchView: SearchView
    private lateinit var analyticsList : ArrayList<Analytics>
    private var filteredAnalyticsList: ArrayList<Analytics> = ArrayList()
    private lateinit var adapter : AnalyticsAdapter
    private lateinit var changeStepView: ChangeStepView




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyticsRequestReserveBinding.inflate(inflater)
        bundle = requireArguments()
        val lab :Lab = bundle.getSerializable("lab") as Lab
        analyticsList = ArrayList()
        analyticsList = lab.Analytics!!
        setAnalytics(analyticsList)
        searchOnLabs()

        return binding.root
    }
    companion object {
        @JvmStatic
        fun newInstance(listener: ChangeStepView) =
            RequestReserveAnalyticsFragment().apply {
                arguments = Bundle().apply {
                    changeStepView=listener
                }
            }
    }
    private fun setAnalytics( analyticsList : ArrayList<Analytics>){
        if (analyticsList.size >0){
            closeProgressBar()
            adapter = AnalyticsAdapter(analyticsList,this)
            val layoutManager = LinearLayoutManager(this.activity)
            binding.rvAnalytics.visibility = View.VISIBLE
            binding.rvAnalytics.layoutManager = layoutManager
            binding.rvAnalytics.adapter = adapter
        }

    }
    private fun searchOnLabs(){
        searchView = binding.searchView


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
        filteredAnalyticsList = ArrayList()
        for(analytic in analyticsList){
            if (labName != null) {
                if (analytic.analysis_name?.lowercase(Locale.getDefault())?.contains(labName.lowercase(Locale.getDefault())) == true)
                    filteredAnalyticsList.add(analytic)
            }
        }
        if (filteredAnalyticsList.isNotEmpty()){
           adapter.setFilteredList(filteredAnalyticsList)
            adapter.notifyDataSetChanged()
        }
    }
     private fun closeProgressBar(){
        SupportFunctions.loading(false, null, binding.progressBar)
    }

    override fun onAnalysisClicked(analyticsList: ArrayList<Analytics>) {
        if (analyticsList.size>0){
            binding.btnSaveAnalytics.visibility = View.VISIBLE
            Log.d("array",analyticsList.size.toString())
        }
        else {
            binding.btnSaveAnalytics.visibility = View.GONE
        }
    }



}