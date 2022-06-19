package com.project.tahlilukclient.fragments
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.FragmentReserveAddressBinding
import com.project.tahlilukclient.listeners.ChangeStepView
import com.project.tahlilukclient.models.Analytics
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.SupportFunctions
import java.util.*
import kotlin.collections.ArrayList
private lateinit var reserveAddressFragmentBinding :FragmentReserveAddressBinding
private lateinit var changeStepView: ChangeStepView
private lateinit var bundle: Bundle
private lateinit var selectedAnalyticsList :ArrayList<Analytics>
private lateinit var lab: Lab



class ReserveAddressFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reserveAddressFragmentBinding = FragmentReserveAddressBinding.inflate(layoutInflater)
       checkPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        if (!SupportFunctions.isGpsEnabled(requireActivity())) {
            SupportFunctions.turnOnGps(requireActivity())
        }

        if (statue == 0) {
            bundle = requireArguments()
            lab = bundle.getSerializable(Constants.SELECTED_LAB) as Lab
            selectedAnalyticsList =
                bundle.getSerializable(Constants.SELECTED_ANALYTICS) as ArrayList<Analytics>

        } else {
            getAddress(currentLatLong!!.latitude, currentLatLong!!.longitude)
            statue = 0
        }
        setListeners()
        getCurrentLocation()

        return reserveAddressFragmentBinding.root
    }

    companion object {
        var currentLatLong: LatLng? = null
        var statue: Int = 0

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
        SupportFunctions.loading(true, null, reserveAddressFragmentBinding.progressBar)
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

    private fun getAddress(latitude: Double, longitude: Double) {
        SupportFunctions.loading(false, null, reserveAddressFragmentBinding.progressBar)
        val addresses: List<Address>
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        val address: String =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        reserveAddressFragmentBinding.tvAddress.text = address
        reserveAddressFragmentBinding.btnSaveAddress.visibility = View.VISIBLE


    }

    private fun setListeners() {
        reserveAddressFragmentBinding.tvChangeLocation.setOnClickListener {
            val mapsFragment = MapsFragment()
            val fragmentManager: FragmentManager =
                (reserveAddressFragmentBinding.root.context as FragmentActivity).supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.fragmentanimation,
                R.anim.fui_slide_out_left,
                R.anim.fragmentanimation,
                R.anim.fui_slide_out_left
            )
            fragmentTransaction.replace(R.id.fragment_container, mapsFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        reserveAddressFragmentBinding.btnSaveAddress.setOnClickListener {
            changeStepView.increaseProgress()
            val confirmReserveFragment = ConfirmReserveFragment()
            val bundle = Bundle()
            bundle.putDouble(Constants.SELECTED_ADDRESS_LATITUDE, currentLatLong!!.latitude)
            bundle.putDouble(Constants.SELECTED_ADDRESS_LONGITUDE, currentLatLong!!.longitude)
            bundle.putSerializable(Constants.SELECTED_ANALYTICS, selectedAnalyticsList)
            bundle.putSerializable(Constants.SELECTED_LAB, lab)
            bundle.putString(
                Constants.RESERVATION_NOTE,
                reserveAddressFragmentBinding.etNotes.text.toString()
            )
            confirmReserveFragment.arguments = bundle
            val fragmentManager: FragmentManager =
                (reserveAddressFragmentBinding.root.context as FragmentActivity).supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.fui_slide_in_right,
                R.anim.fragmentanimation,
                R.anim.fui_slide_in_right,
                R.anim.fragmentanimation
            )
            fragmentTransaction.replace(R.id.fragment_container, confirmReserveFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }
    private fun checkPermission(){
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    getCurrentLocation()
                } else {
//              Toast.makeText(requireContext(),"pleast",Toast.LENGTH_LONG).show()
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }

            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }
}