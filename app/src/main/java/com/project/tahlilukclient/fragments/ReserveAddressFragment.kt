package com.project.tahlilukclient.fragments
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.project.tahlilukclient.databinding.FragmentReserveAddressBinding
import com.project.tahlilukclient.listeners.ChangeStepView
import com.project.tahlilukclient.models.Analytics
import com.project.tahlilukclient.models.Lab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.collections.ArrayList
private lateinit var reserveAddressFragmentBinding :FragmentReserveAddressBinding
private lateinit var changeStepView: ChangeStepView
private lateinit var bundle: Bundle
private lateinit var selectedAnalyticsList :ArrayList<Analytics>
private lateinit var lab: Lab
var currentLatLong: LatLng? = null
private val coroutineScope = CoroutineScope(Dispatchers.IO)


class ReserveAddressFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reserveAddressFragmentBinding = FragmentReserveAddressBinding.inflate(inflater)
        getCurrentLocation()

        return reserveAddressFragmentBinding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(listener: ChangeStepView) =
            ReserveAddressFragment().apply {
                arguments = Bundle().apply {
                    changeStepView = listener

                }
            }
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        LocationServices.getFusedLocationProviderClient(requireActivity())
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        val index = locationResult.locations.size - 1
                        val latitude = locationResult.locations[index].latitude
                        val longitude = locationResult.locations[index].longitude
                        currentLatLong = LatLng(latitude, longitude)
                        getAddress(currentLatLong!!.latitude, currentLatLong!!.longitude)
                    }
                }
            }, Looper.getMainLooper())
    }

    private fun getAddress(latitude:Double,longitude:Double){
        val addresses: List<Address>
        val geocoder: Geocoder = Geocoder(requireContext(), Locale.getDefault())
        addresses = geocoder.getFromLocation(latitude , longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        val address: String = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        reserveAddressFragmentBinding.tvAddress.setText(address)


    }
}