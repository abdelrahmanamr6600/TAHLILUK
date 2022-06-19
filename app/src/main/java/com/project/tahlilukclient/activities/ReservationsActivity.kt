package com.project.tahlilukclient.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ActivityReservationsBinding
import com.project.tahlilukclient.fragments.PatientReservationsFragment

class ReservationsActivity : AppCompatActivity() {
    lateinit var binding:ActivityReservationsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationsBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setListeners()
        replaceFragment(PatientReservationsFragment.newInstance())
        checkPermission()
    }


    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = this.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.fui_slide_in_right,R.anim.fragmentanimation,R.anim.fui_slide_in_right,R.anim.fragmentanimation)
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.isAddToBackStackAllowed
        fragmentTransaction.commit()
    }

    private fun setListeners(){
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
    }


    private fun checkPermission(){
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {

                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                } else -> {
                // No location access granted.
            }
            }
        }

        if ( ContextCompat.checkSelfPermission(
               this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED){
        }
        else{
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }
}