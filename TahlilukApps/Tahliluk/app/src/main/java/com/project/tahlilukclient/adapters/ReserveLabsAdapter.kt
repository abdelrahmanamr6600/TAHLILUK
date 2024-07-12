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
import com.project.tahlilukclient.databinding.ItemContainerLabBinding
import com.project.tahlilukclient.listeners.LabListener
import com.project.tahlilukclient.models.Lab


class ReserveLabsAdapter(private var labs: ArrayList<Lab>, private var listener: LabListener) :
    RecyclerView.Adapter<ReserveLabsAdapter.LabViewHolder>() {

    inner class LabViewHolder(var binding: ItemContainerLabBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setLabData(lab: Lab) {
            binding.textLabName.text = lab.labName
            binding.textLabAddress.text = lab.address
            binding.imageProfile.setImageBitmap(getLabImage(lab.image!!))
            binding.root.setOnClickListener {
                listener.onLabClicked(lab)

            }
        }

    }

    private fun getLabImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabViewHolder {
        val binding =
            ItemContainerLabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabViewHolder, position: Int) {
        holder.setLabData(labs[position])
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rv_animation))


    }

    override fun getItemCount(): Int {
        return labs.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(FilteredList:ArrayList<Lab>){
        this.labs = FilteredList
        notifyDataSetChanged()
    }



}