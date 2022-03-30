package com.project.tahlilukclient.activities

import android.graphics.*
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ActivityMapBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.SupportFunctions


class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var activityReserveBinding: ActivityMapBinding
    private lateinit var clientLocation: FusedLocationProviderClient
    private var mLabsList: ArrayList<Lab> = ArrayList()
    private var currentLatLong: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityReserveBinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(activityReserveBinding.root)
        SupportFunctions.fullScreen(window)
        initObjects()
        setListeners()
    }

    private fun initObjects() {
        val latitude = intent.getDoubleExtra(Constants.KEY_CURRENT_LATITUDE, -1.0)
        val longitude = intent.getDoubleExtra(Constants.KEY_CURRENT_LONGITUDE, -1.0)
        currentLatLong = LatLng(latitude, longitude)

        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        clientLocation = LocationServices.getFusedLocationProviderClient(this)
    }


    private fun setListeners() {
        activityReserveBinding.rg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                activityReserveBinding.rbList.id -> {
                    onBackPressed()
                }
            }
        }
    }

    private fun getLabsFromFireStore() {
        FirestoreClass().getLabs(this, Constants.Key_COLLECTION_LABS)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
    }

    fun placeMarkerOnMap(labsList: ArrayList<Lab>) {
        mLabsList = labsList
        for (m in labsList) {
            val labLatLng = LatLng(m.labLatitude!!.toDouble(), m.labLongitude!!.toDouble())
            val marker = MarkerOptions().position(labLatLng)
                .icon(BitmapDescriptorFactory.fromBitmap(getCircleBitmap(getLabImage(m.image!!))!!))
            mMap.addMarker(marker)
            val markerOptions = MarkerOptions().position(currentLatLong!!)
                .icon(BitmapDescriptorFactory.defaultMarker())
            markerOptions.title("$currentLatLong")
            mMap.addMarker(markerOptions)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong!!, 13f))
        }

    }

    override fun onMarkerClick(p0: Marker): Boolean {
        Toast.makeText(this, "hello", Toast.LENGTH_LONG).show()
        return true
    }

    fun successLabFromFireStore(labsList: ArrayList<Lab>) {
        mLabsList = labsList

    }

    private fun getLabImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap? {
        val width = 120
        val height = 120
        val output = Bitmap.createBitmap(
            width,
            height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, width, height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        bitmap.recycle()
        return output
    }


    override fun onResume() {
        super.onResume()
        getLabsFromFireStore()
        activityReserveBinding.rbMap.isChecked = true
    }
}



