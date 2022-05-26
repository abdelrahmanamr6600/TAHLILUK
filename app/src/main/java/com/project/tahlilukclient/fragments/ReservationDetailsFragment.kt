package com.project.tahlilukclient.fragments
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.tahlilukclient.R
import com.project.tahlilukclient.adapters.ConfirmAnalyticsAdapter
import com.project.tahlilukclient.databinding.FragmentReservationDetailsBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.models.AnalysisResult
import com.project.tahlilukclient.models.Reserve
import java.util.*

class ReservationDetailsFragment : Fragment() {
    private lateinit var reservationDetailsBinding: FragmentReservationDetailsBinding
    private lateinit var bundle: Bundle
    private lateinit var reservation:Reserve
    lateinit var adapter: ConfirmAnalyticsAdapter
    lateinit var image:String
    lateinit var labName:String

    companion object {

        @JvmStatic
        fun newInstance() =
            ReservationDetailsFragment().apply {
                arguments = Bundle().apply {


                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         bundle=requireArguments()
        reservation = bundle.getSerializable("reservation") as Reserve
        image = bundle.getString("image")!!
        labName = bundle.getString("labName")!!

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        reservationDetailsBinding = FragmentReservationDetailsBinding.inflate(layoutInflater)
        setReservationData()
        setListeners()
        return reservationDetailsBinding.root
    }

    private fun setReservationData(){
       reservationDetailsBinding.tvOrderLabName.text = labName
        reservationDetailsBinding.tvOrderDate.text = reservation.orderDateTime
        reservationDetailsBinding.tvCheckoutSubTotal.text = reservation.orderAnalyticsPrice
        reservationDetailsBinding.tvCheckoutTotalAmount.text=reservation.orderTotalAmount
        reservationDetailsBinding.tvConfirmAdditionalNote.text=reservation.orderAdditionalInformation
        reservationDetailsBinding.tvConfirmAddress.text = reservation.orderAddress
        reservationDetailsBinding.tvCheckoutShippingCharge.text =getString(R.string.visit_price)
        when(reservation.orderState){
            "pending"  -> reservationDetailsBinding.tvOrderState.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.error_color))
            "in progress"  -> reservationDetailsBinding.tvOrderState.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.error))
            "Accepted" -> reservationDetailsBinding.tvOrderState.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.primary_text))
        }
        reservationDetailsBinding.tvOrderState.text = reservation.orderState
        reservationDetailsBinding.ivLabImage.setImageBitmap(getLabImage(image))
        adapter = ConfirmAnalyticsAdapter(reservation.analyticsList!!)
        reservationDetailsBinding.rvCartListItems.layoutManager = LinearLayoutManager(requireContext())
        reservationDetailsBinding.rvCartListItems.adapter = adapter
        if (reservation.resultsList== null){
            reservationDetailsBinding.CvResult.visibility =View.GONE
        }
        else
        {
            reservationDetailsBinding.CvResult.visibility =View.VISIBLE
        }

    }

private fun setListeners(){
    reservationDetailsBinding.btnCancelRequest.setOnClickListener {
        FirestoreClass().deleteReservation(this,reservation.orderId!!)
        requireActivity().onBackPressed()
    }

    reservationDetailsBinding.btnShowResult.setOnClickListener {
        val resultFragment  = ResultsFragment.newInstance()
        val bundle = Bundle()
        bundle.putString("labImage",image)
        bundle.putSerializable("list",reservation.resultsList)
        resultFragment.arguments = bundle
        val fragmentManager: FragmentManager =
            (reservationDetailsBinding.root.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.fui_slide_in_right,R.anim.fragmentanimation,R.anim.fui_slide_in_right,R.anim.fragmentanimation)
        fragmentTransaction.replace(R.id.fragment_container, resultFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}
    private fun getLabImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

}