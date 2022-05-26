package com.project.tahlilukclient.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.tahlilukclient.R
import com.project.tahlilukclient.activities.ReserveActivity
import com.project.tahlilukclient.adapters.ReservationsAdapter
import com.project.tahlilukclient.databinding.DialogProgressBinding
import com.project.tahlilukclient.databinding.FragmentPatientReservationsBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.listeners.ReservationListener
import com.project.tahlilukclient.models.Reserve
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.project.tahlilukclient.utilities.SupportFunctions
import kotlinx.coroutines.*
import kotlin.collections.ArrayList


class PatientReservationsFragment : Fragment(),ReservationListener {
    private lateinit var binding:FragmentPatientReservationsBinding
    private lateinit var reservationsAdapter :ReservationsAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var bindingDialog: DialogProgressBinding
    private lateinit var patientId:String
    lateinit var image:String
    lateinit var labName : String
    private var parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPatientReservationsBinding.inflate(layoutInflater)
        bindingDialog = DialogProgressBinding.inflate(layoutInflater)
        preferenceManager =PreferenceManager(requireContext())
        patientId  = preferenceManager.getString(Constants.KEY_PATIENT_ID).toString()
        SupportFunctions.loading(true,null,binding.progressBar)
        getReservationsFromFireStore()
        setListeners()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PatientReservationsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }



    fun successReservationsFromFireStore(reservationsList: ArrayList<Reserve>) {
        if (reservationsList.size >0){
            reservationsList.sortWith { obj1: Reserve, obj2: Reserve ->
                obj2.orderDateTime!!.compareTo(obj1.orderDateTime!!)
            }
            SupportFunctions.loading(false,null,binding.progressBar)
            binding.rvReservations.visibility = View.VISIBLE
            reservationsAdapter = ReservationsAdapter(reservationsList,this)
            binding.rvReservations.layoutManager = LinearLayoutManager(this.activity)

            binding.rvReservations.adapter = reservationsAdapter
        } else{
            SupportFunctions.loading(false,null,binding.progressBar)
            binding.tvNoReservations.visibility =View.VISIBLE
            binding.tvReserveNow.visibility =View.VISIBLE


        }

    }


    private fun getReservationsFromFireStore(){
        FirestoreClass().getReservations(this,Constants.KEY_COLLECTION_RESERVATION,patientId)
    }

    override fun onReservationClickListener(reserve: Reserve) {
        SupportFunctions.showProgressBar(
            requireContext(),
            resources.getString(R.string.please_wait),
            bindingDialog.tvProgressText
        )

        FirestoreClass().getLabImage(this,reserve.labId!!)
        coroutineScope.launch {
            delay(420)
            val reservationDetailsFragment  = ReservationDetailsFragment.newInstance()
            val bundle = Bundle()
            bundle.putSerializable("reservation",reserve)
            bundle.putString("image",image)
            bundle.putString("labName",labName)
            reservationDetailsFragment.arguments = bundle
            val fragmentManager: FragmentManager =
                (binding.root.context as FragmentActivity).supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fui_slide_in_right,R.anim.fragmentanimation,R.anim.fui_slide_in_right,R.anim.fragmentanimation)
            fragmentTransaction.replace(R.id.fragment_container, reservationDetailsFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
            SupportFunctions.hideDialog()
        }

    }


    private fun setListeners(){
        binding.tvReserveNow.setOnClickListener {
            val intent = Intent(requireContext(),ReserveActivity::class.java)
            this.activity?.startActivity(intent)
            this.activity?.finish()
        }
    }

    fun setLabImage(image:String,labName:String){
       this.image = image
        this.labName = labName
    }
}