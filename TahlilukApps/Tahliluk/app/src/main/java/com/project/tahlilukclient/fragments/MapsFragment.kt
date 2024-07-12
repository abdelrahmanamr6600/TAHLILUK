package com.project.tahlilukclient.fragments

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.FragmentMapsBinding
import java.io.IOException
import java.util.*

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraIdleListener {

    private lateinit var mapsFragmentMapsBinding: FragmentMapsBinding
    private lateinit var map: GoogleMap


    private val callback = OnMapReadyCallback { googleMap ->

        map = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ReserveAddressFragment.currentLatLong!!))
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                ReserveAddressFragment.currentLatLong!!,
                15f
            )
        )

        googleMap.setOnCameraIdleListener(this)
        googleMap.setOnCameraMoveListener(this)
        googleMap.setOnCameraMoveStartedListener(this)

        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {

            }

            override fun onMarkerDragEnd(p0: Marker) {

            }

            override fun onMarkerDragStart(p0: Marker) {
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mapsFragmentMapsBinding = FragmentMapsBinding.inflate(inflater)
        setListeners()
        return mapsFragmentMapsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onCameraMove() {
    }

    override fun onCameraMoveStarted(p0: Int) {
    }

    override fun onCameraIdle() {

        val addresses: List<Address>
        val geocoder = Geocoder(
            requireContext(), Locale
                .getDefault()
        )
        try {
            addresses = geocoder.getFromLocation(
                map.cameraPosition.target.latitude,
                map.cameraPosition.target.longitude,
                1
            )

            val address: String = addresses[0].getAddressLine(0)
            mapsFragmentMapsBinding.tvAddress.text = address
            ReserveAddressFragment.currentLatLong =
                LatLng(addresses[0].latitude, addresses[0].longitude)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var address: List<Address>? = null
        try {
            address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        ReserveAddressFragment.currentLatLong = LatLng(address!![0].latitude, address[0].longitude)
    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        TODO("Not yet implemented")
    }

    private fun setListeners() {
        mapsFragmentMapsBinding.btnSaveAddress.setOnClickListener {
            val reserveAddressFragment = ReserveAddressFragment()
            ReserveAddressFragment.statue = 1
            val fragmentManager: FragmentManager =
                (mapsFragmentMapsBinding.root.context as FragmentActivity).supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, reserveAddressFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

}