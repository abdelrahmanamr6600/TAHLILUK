package com.project.tahlilukclient.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.tahlilukclient.activities.*
import com.project.tahlilukclient.databinding.FragmentHomeBinding
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.project.tahlilukclient.utilities.SupportFunctions


class HomeFragment : Fragment() {
    lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(requireContext())
        arguments?.let {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)
        return fragmentHomeBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPatientDetails()
        setListeners()

    }

    private fun loadPatientDetails() {
        fragmentHomeBinding.textWelcomePatient.text =
            "${preferenceManager.getString(Constants.KEY_FIRSTNAME)}"
        val bytes: ByteArray =
            Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        fragmentHomeBinding.imageProfile.setImageBitmap(bitmap)
    }

    private fun setListeners() {
        fragmentHomeBinding.cvChat.setOnClickListener {
            startMainChatActivity()
        }

        fragmentHomeBinding.cvReserve.setOnClickListener {
        }
        fragmentHomeBinding.cvLabs.setOnClickListener {
            startLabsActivity()
        }
        fragmentHomeBinding.cvReserve.setOnClickListener {
            startReserveActivity()
        }
        fragmentHomeBinding.cvReservation.setOnClickListener {
            startReservationsActivity()
        }
        fragmentHomeBinding.cvGetReady.setOnClickListener {
            startGetReadyActivity()
        }
        fragmentHomeBinding.cvCheckUps.setOnClickListener {
            startCheckupsActivity()
        }
    }

    private fun startMainChatActivity() {
        if (SupportFunctions.checkForInternet(requireContext())) {
            val intent = Intent(view?.context, MainChatActivity::class.java)
            startActivity(intent)
        } else {
            SupportFunctions.showNoInternetSnackBar(fragmentHomeBinding)
        }
    }

    private fun startLabsActivity() {
        if (SupportFunctions.checkForInternet(requireContext())) {
            val intent = Intent(view?.context, LabsActivity::class.java)
            startActivity(intent)
        } else {
            SupportFunctions.showNoInternetSnackBar(fragmentHomeBinding)
        }
    }

    private fun startReserveActivity() {
        if (SupportFunctions.checkForInternet(requireContext()))  {
            val intent = Intent(requireContext(), ReserveActivity::class.java)
            startActivity(intent)
        }
        else
        {
            SupportFunctions.showNoInternetSnackBar(fragmentHomeBinding)
        }
    }

    private fun startReservationsActivity(){

        if (SupportFunctions.checkForInternet(requireContext()))  {
            val intent = Intent(requireContext(), ReservationsActivity::class.java)
            startActivity(intent)
        }
        else
        {
            SupportFunctions.showNoInternetSnackBar(fragmentHomeBinding)
        }

    }


    private fun startGetReadyActivity(){

        if (SupportFunctions.checkForInternet(requireContext()))  {
            val intent = Intent(requireContext(), GetReadyActivity::class.java)
            startActivity(intent)
        }
        else
        {
            SupportFunctions.showNoInternetSnackBar(fragmentHomeBinding)
        }

    }


    private fun startCheckupsActivity(){

        if (SupportFunctions.checkForInternet(requireContext()))  {
            val intent = Intent(requireContext(), CheckupsActivity::class.java)
            startActivity(intent)
        }
        else
        {
            SupportFunctions.showNoInternetSnackBar(fragmentHomeBinding)
        }

    }

}




