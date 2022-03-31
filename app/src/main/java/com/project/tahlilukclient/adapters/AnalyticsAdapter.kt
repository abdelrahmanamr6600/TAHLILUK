package com.project.tahlilukclient.adapters
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ItemContainerAnalysesBinding
import com.project.tahlilukclient.databinding.ItemContainerLabBinding
import com.project.tahlilukclient.listeners.LabListener
import com.project.tahlilukclient.models.Analytics
import com.project.tahlilukclient.models.Lab


class AnalyticsAdapter(private var analytics: ArrayList<Analytics>) :
    RecyclerView.Adapter<AnalyticsAdapter.LabViewHolder>() {

    inner class LabViewHolder(var binding: ItemContainerAnalysesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setLabData(analyses: Analytics) {
            binding.textAnalysisName.text = analyses.analysis_name
            binding.textAnalysisPrice.text = analyses.analysis_price


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabViewHolder {
        val binding =
            ItemContainerAnalysesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabViewHolder, position: Int) {
        holder.setLabData(analytics[position])
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



}