package com.project.tahlilukclient.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.project.tahlilukclient.R
import com.project.tahlilukclient.activities.LabsActivity
import com.project.tahlilukclient.listeners.LabListener
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.SupportFunctions
import android.os.Looper
import kotlinx.coroutines.*
import com.google.android.gms.location.*
import com.project.tahlilukclient.databinding.ItemContainerLabCardBinding


class LabsInfoAdapter(
    var activity: LabsActivity,
    private var labs: ArrayList<Lab>,
    var labListener: LabListener,
    var state: Boolean
) :
    RecyclerView.Adapter<LabsInfoAdapter.LabViewHolder>() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    var currentLatLong: LatLng? = null
    private var mapState = false


    init {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED && SupportFunctions.isGpsEnabled(activity)
        ) {

            coroutineScope.launch {
                getCurrentLocation()
            }
        }

        if (state) {
            mapState = state
            state = false
        }
    }

    inner class LabViewHolder(var binding: ItemContainerLabCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setLabData(lab: Lab) {
            binding.textLabName.text = lab.labName
            binding.textLabAddress.text = lab.address
            binding.ivLabProfile.setImageBitmap(getLabImage(lab.image!!))
            binding.ivOpenChat.setOnClickListener {
                labListener.onLabClicked(lab)
            }
            binding.ivOpenMap.setOnClickListener {
                openMap(lab)
            }
            binding.ivReqReserve.setOnClickListener {
                LabsActivity.statue = 1
                labListener.onLabClicked(lab)
            }



            reTouchAfterDelay(binding)
        }
    }

    private fun getLabImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabViewHolder {
        val binding =
            ItemContainerLabCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return LabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabViewHolder, position: Int) {
        holder.setLabData(labs[position])
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.rv_animation
            )
        )


    }

    override fun getItemCount(): Int {
        return labs.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(FilteredList: ArrayList<Lab>) {
        this.labs = FilteredList
        notifyDataSetChanged()

    }

    private fun goToMap(lab: Lab) {
        val uri =
            Uri.parse("https://www.google.co.in/maps/dir/${currentLatLong!!.latitude},${currentLatLong!!.longitude}/${lab.latitude},${lab.longitude}")
        val dirIntent = Intent(Intent.ACTION_VIEW, uri)
        dirIntent.setPackage("com.google.android.apps.maps")
        dirIntent.resolveActivity(activity.packageManager).let {
            activity.startActivity(dirIntent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        LocationServices.getFusedLocationProviderClient(activity)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(activity)
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        val index = locationResult.locations.size - 1
                        val latitude = locationResult.locations[index].latitude
                        val longitude = locationResult.locations[index].longitude
                        currentLatLong = LatLng(latitude, longitude)
                    }
                }
            }, Looper.getMainLooper())
    }

    private fun openMap(lab: Lab) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            SupportFunctions.getPermission(activity)
        } else if (!SupportFunctions.isGpsEnabled(activity)) {
            SupportFunctions.turnOnGps(activity)

        } else {
            if (currentLatLong != null) {
                SupportFunctions.showSwitcher(false, activity.activityLabsBinding.rg)
                goToMap(lab)
            } else {
                try {
                    activity.count = if (activity.count != 3) {
                        activity.reloadRecyclerView(true)
                        activity.count.plus(1)
                    } else {
                        SupportFunctions.showSwitcher(true, activity.activityLabsBinding.rg)
                        SupportFunctions.showDialog(activity, true)
                        0
                    }
                } catch (ex: Exception) {
                }
            }
        }
    }

    private fun reTouchAfterDelay(binding: ItemContainerLabCardBinding) {
        if (mapState) {
            SupportFunctions.showSwitcher(false, activity.activityLabsBinding.rg)
            coroutineScope.launch {
                delay(2000)
                withContext(Dispatchers.Main) {
                    binding.ivOpenMap.performClick()
                }
            }
            mapState = false
        }
    }

}
