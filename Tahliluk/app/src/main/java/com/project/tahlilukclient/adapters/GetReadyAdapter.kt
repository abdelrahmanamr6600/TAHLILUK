package com.project.tahlilukclient.adapters

import GetReady
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ItemContainerGetreadyBinding
import com.project.tahlilukclient.listeners.GetReadyListener

class GetReadyAdapter : RecyclerView.Adapter<GetReadyAdapter.ItemViewHolder> {
    private lateinit var itemsList:ArrayList<GetReady>
    private lateinit var getReadyListener:GetReadyListener
    constructor()
    constructor(itemsList:ArrayList<GetReady>,getReadyListener:GetReadyListener){
        this.itemsList = itemsList
        this.getReadyListener = getReadyListener
    }

    inner class ItemViewHolder(var binding: ItemContainerGetreadyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setItemDetails(item: GetReady) {
        binding.getReadyTitle.text = item.title
            binding.root.setOnClickListener {
                getReadyListener.getReadyItem(item)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemContainerGetreadyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setItemDetails(itemsList[position])
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rv_animation))
    }

    override fun getItemCount(): Int {
      return itemsList.size
    }


}