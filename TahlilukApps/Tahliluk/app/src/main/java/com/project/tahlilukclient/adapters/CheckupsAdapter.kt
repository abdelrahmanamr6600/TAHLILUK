package com.project.tahlilukclient.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ItemContainerCheckupsBinding
import com.project.tahlilukclient.listeners.CheckupsListener
import com.project.tahlilukclient.models.Checkups

class CheckupsAdapter : RecyclerView.Adapter<CheckupsAdapter.ItemViewHolder> {
    private var itemsList: ArrayList<Checkups>
    private var checkupsListener: CheckupsListener

    constructor(itemsList: ArrayList<Checkups>, checkupsListener: CheckupsListener) {
        this.itemsList = itemsList
        this.checkupsListener = checkupsListener
    }

    inner class ItemViewHolder(var binding: ItemContainerCheckupsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setItemDetails(item: Checkups) {
            binding.checkupTitle.text = item.title
            binding.root.setOnClickListener {
                checkupsListener.checkupItem(item)

            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckupsAdapter.ItemViewHolder {
        val binding =
            ItemContainerCheckupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckupsAdapter.ItemViewHolder, position: Int) {
        holder.setItemDetails(itemsList[position])
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.rv_animation
            )
        )
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }


}