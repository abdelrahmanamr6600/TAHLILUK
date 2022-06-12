package com.project.tahlilukclient.fragments

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.project.tahlilukclient.R
import com.project.tahlilukclient.adapters.ConfirmAnalyticsAdapter
import com.project.tahlilukclient.databinding.FragmentConfirmReserveBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.listeners.ChangeStepView
import com.project.tahlilukclient.models.Analytics
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.models.Reserve
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.project.tahlilukclient.utilities.SupportFunctions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ConfirmReserveFragment : Fragment() {
    private lateinit var confirmReserveBinding: FragmentConfirmReserveBinding
    private lateinit var changeStepView: ChangeStepView
    private lateinit var bundle: Bundle
    private lateinit var selectedAnalyticsList: ArrayList<Analytics>
    private lateinit var currentLocation: LatLng
    private lateinit var lab: Lab
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var note: String
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private val mVisitingFee: Double = 50.0
    var address: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (SupportFunctions.isGpsEnabled(requireActivity())) {
            SupportFunctions.turnOnGps(requireActivity())
        }
        setListeners()
        setAddressSection()
        setAnalyticsList()
        setReceiptSection()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        preferenceManager = PreferenceManager(requireContext())
        bundle = requireArguments()
        confirmReserveBinding = FragmentConfirmReserveBinding.inflate(inflater)
        currentLocation = LatLng(
            bundle.getDouble(Constants.SELECTED_ADDRESS_LATITUDE),
            bundle.getDouble(Constants.SELECTED_ADDRESS_LONGITUDE)
        )
        lab = bundle.getSerializable(Constants.SELECTED_LAB) as Lab
        (bundle.getSerializable(Constants.SELECTED_ANALYTICS) as ArrayList<Analytics>).also {
            selectedAnalyticsList = it
        }
        note = bundle.getString(Constants.RESERVATION_NOTE).toString()
        return confirmReserveBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: ChangeStepView) =
            ConfirmReserveFragment().apply {
                arguments = Bundle().apply {
                    changeStepView = listener
                }
            }
    }

    private fun setAddressSection() {
        getAddress(currentLocation.latitude, currentLocation.longitude)
        confirmReserveBinding.tvConfirmAdditionalNote.text = note
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        val addresses: List<Address>
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        address =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        confirmReserveBinding.tvConfirmAddress.text = address

    }

    private fun setAnalyticsList() {
        if (selectedAnalyticsList.isNotEmpty()) {
            confirmReserveBinding.rvCartListItems.visibility = View.VISIBLE
            val adapter = ConfirmAnalyticsAdapter(selectedAnalyticsList)
            confirmReserveBinding.rvCartListItems.layoutManager =
                LinearLayoutManager(requireContext())
            confirmReserveBinding.rvCartListItems.adapter = adapter
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setReceiptSection() {
        for (analysis in selectedAnalyticsList) {
            mSubTotal += analysis.analysis_price!!.toDouble()
        }
        (mSubTotal.toString() + getString(R.string.egyptian_currency)).also {
            confirmReserveBinding.tvCheckoutSubTotal.text = it
        }
        ("50" + getString(R.string.egyptian_currency)).also {
            confirmReserveBinding.tvCheckoutShippingCharge.text = it
        }

        mTotalAmount = mSubTotal + mVisitingFee
        confirmReserveBinding.tvCheckoutTotalAmount.text =
            mTotalAmount.toString() + getString(R.string.egyptian_currency)
    }


    private fun setListeners() {
        confirmReserveBinding.btnSaveAnalytics.setOnClickListener {
            SupportFunctions.loading(
                true,
                confirmReserveBinding.btnSaveAnalytics,
                confirmReserveBinding.progressBar
            )
            val reserve = Reserve()
            reserve.labId = lab.id
            reserve.analyticsList = selectedAnalyticsList
            reserve.orderLocation = GeoPoint(currentLocation.latitude, currentLocation.longitude)
            reserve.orderAddress = address
            reserve.orderState = resources.getString(R.string.pending)
            reserve.patientId = preferenceManager.getString(Constants.KEY_PATIENT_ID)
            reserve.orderUserPhone = preferenceManager.getString(Constants.KEY_PHONE_NUMBER)
            reserve.orderAnalyticsPrice = confirmReserveBinding.tvCheckoutSubTotal.text.toString()
            reserve.orderTotalAmount = confirmReserveBinding.tvCheckoutTotalAmount.text.toString()
            reserve.orderDateTime = getTime()
            reserve.orderAdditionalInformation = note
            FirestoreClass().addReserve(this, reserve, Constants.KEY_COLLECTION_RESERVATION)
        }
    }

    fun addReserve() {
        SupportFunctions.loading(false, null, confirmReserveBinding.progressBar)

        val finishReservationFragment = FinishReservationFragment.newInstance()
        val bundle = Bundle()
        bundle.putString("phone", preferenceManager.getString(Constants.KEY_PHONE_NUMBER))
        finishReservationFragment.arguments = bundle
        val fragmentManager: FragmentManager =
            (confirmReserveBinding.root.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.fui_slide_in_right,
            R.anim.fragmentanimation,
            R.anim.fui_slide_in_right,
            R.anim.fragmentanimation
        )
        fragmentTransaction.replace(R.id.fragment_container, finishReservationFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }

    private fun getTime(): String {
        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "MMMM dd, yyyy - hh:mm a"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        return formatter.format(calendar.time)

    }
}


