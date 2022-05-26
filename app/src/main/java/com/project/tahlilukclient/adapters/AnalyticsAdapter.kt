package com.project.tahlilukclient.adapters
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ItemContainerAnalysesBinding
import com.project.tahlilukclient.listeners.AnalyticsListener
import com.project.tahlilukclient.listeners.LabListener
import com.project.tahlilukclient.models.Analytics


class AnalyticsAdapter(private var analytics: ArrayList<Analytics>, private var listener: AnalyticsListener) :
    RecyclerView.Adapter<AnalyticsAdapter.LabViewHolder>() {

    private var analyticsList:ArrayList<Analytics> = ArrayList()

    inner class LabViewHolder(var binding: ItemContainerAnalysesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setAnalyticData(analyses: Analytics) {
            binding.textAnalysisName.text = analyses.analysis_name
            binding.textAnalysisPrice.text = analyses.analysis_price
            binding.ivSelected.setOnClickListener {
                selectAnalysis(binding.ivSelected,analyses)
                listener.onAnalysisClicked(analyticsList)
            }
            binding.root.setOnClickListener {
             selectAnalysis(binding.ivSelected,analyses)
                listener.onAnalysisClicked(analyticsList)
            }




        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabViewHolder {
        val binding =
            ItemContainerAnalysesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabViewHolder, position: Int) {
        holder.setAnalyticData(analytics[position])
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rv_animation))


    }

    override fun getItemCount(): Int {
        return analytics.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(FilteredList:ArrayList<Analytics>){
        this.analytics = FilteredList
        notifyDataSetChanged()
    }


    private fun selectAnalysis(check:CheckBox, analysis:Analytics){
        when(check.isChecked)
        {
            false ->{
                check.isChecked=true
                analyticsList.add(analysis)
                Log.d("size",analyticsList.size.toString())
            }
            true ->{
                check.isChecked=false
                analyticsList.remove(analysis)
                Log.d("size",analyticsList.size.toString())

            }

        }
    }



}