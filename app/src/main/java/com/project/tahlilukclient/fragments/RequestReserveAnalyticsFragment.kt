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
import com.project.tahlilukclient.adapters.AnalyticsAdapter
import com.project.tahlilukclient.databinding.FragmentAnalyticsRequestReserveBinding
import com.project.tahlilukclient.listeners.AnalyticsListener
import com.project.tahlilukclient.listeners.ChangeStepView
import com.project.tahlilukclient.models.Analytics
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.SupportFunctions
import java.util.*



class RequestReserveAnalyticsFragment : Fragment(),AnalyticsListener {
    private lateinit var binding : FragmentAnalyticsRequestReserveBinding
    private lateinit var bundle: Bundle
    private lateinit var searchView: SearchView

    private var filteredAnalyticsList: ArrayList<Analytics> = ArrayList()
    private lateinit var adapter : AnalyticsAdapter
    private lateinit var changeStepView: ChangeStepView
    private lateinit var lab:Lab
    private lateinit var selectedAnalyticsList :ArrayList<Analytics>




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyticsRequestReserveBinding.inflate(inflater)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bundle = requireArguments()
        lab  = bundle.getSerializable(Constants.SELECTED_LAB) as Lab
        setAnalytics( lab.Analytics!!)
        searchOnLabs()
        setListeners()
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
        else{

            binding.TVNoAnalytics.visibility = View.VISIBLE
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
        for(analytic in lab.Analytics!!){
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
        selectedAnalyticsList = ArrayList()
        if (analyticsList.size>0){
            selectedAnalyticsList = analyticsList
            binding.btnSaveAnalytics.visibility = View.VISIBLE

        }
        else {
            selectedAnalyticsList = analyticsList
            binding.btnSaveAnalytics.visibility = View.GONE
        }
    }

    private fun nextStep(){
        changeStepView.increaseProgress()
        val reserveAddressFragment =ReserveAddressFragment.newInstance(changeStepView)
        val bundle = Bundle()
        bundle.putSerializable(Constants.SELECTED_LAB,lab)
        bundle.putSerializable(Constants.SELECTED_ANALYTICS,selectedAnalyticsList)
        reserveAddressFragment.arguments = bundle
        val fragmentManager: FragmentManager =
            (binding.root.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.fui_slide_in_right,R.anim.fragmentanimation,R.anim.fui_slide_in_right,R.anim.fragmentanimation)
        fragmentTransaction.replace(R.id.fragment_container, reserveAddressFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }



    private fun setListeners(){
        binding.btnSaveAnalytics.setOnClickListener {
            nextStep()
        }

    }
}