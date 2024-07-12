package com.project.tahlilukclient.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ItemContainerReservationsBinding
import com.project.tahlilukclient.listeners.LabListener
import com.project.tahlilukclient.listeners.ReservationListener
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.models.Reserve
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager

class ReservationsAdapter : RecyclerView.Adapter<ReservationsAdapter.ReservationsViewHolder> {
    private lateinit var fragment: Fragment
    private lateinit var reservationsList: ArrayList<Reserve>
    private lateinit var listener: ReservationListener
    private lateinit var preferenceManager: PreferenceManager

    constructor()
    constructor(
        fragment: Fragment,
        reservationsList: ArrayList<Reserve>,
        listener: ReservationListener
    ) {
        this.reservationsList = reservationsList
        this.listener = listener
        this.fragment = fragment
    }


    inner class ReservationsViewHolder(var binding: ItemContainerReservationsBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun setReservationData(reserve: Reserve) {
            preferenceManager = PreferenceManager(fragment.requireContext())
            binding.tvOrderprice.text = reserve.orderTotalAmount
            if (preferenceManager.getString(Constants.KEY_DEVICE_LANGUAGE) == Constants.KEY_LANGUAGE_ARABIC_SYSTEM) {
                when (reserve.orderState) {
                    "Pending" -> binding.tvOrderState.text =
                        fragment.resources.getText(R.string.pending)
                    "In Progress" -> binding.tvOrderState.text =
                        fragment.resources.getText(R.string.in_progress)
                    "Completed" -> binding.tvOrderState.text =
                        fragment.resources.getText(R.string.completed)
                }
            } else {
                binding.tvOrderState.text = reserve.orderState
            }
            binding.tvOrderAddress.text = reserve.orderAddress
            binding.tvOrderDate.text = reserve.orderDateTime

            when (reserve.orderState) {
                "Pending" -> binding.tvOrderState.setTextColor(Color.parseColor("#B00020"))
                "In Progress" -> binding.tvOrderState.setTextColor(Color.parseColor("#284693"))
                "Completed" -> binding.tvOrderState.setTextColor(Color.parseColor("#284693"))
            }

            binding.root.setOnClickListener {
                listener.onReservationClickListener(reserve)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationsViewHolder {
        val binding =
            ItemContainerReservationsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ReservationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReservationsViewHolder, position: Int) {
        holder.setReservationData(reservationsList[position])
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.rv_animation
            )
        )
    }

    override fun getItemCount(): Int {
        return reservationsList.size
    }
}