package com.project.tahlilukclient.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.gms.location.*
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ActivityMainBinding
import com.project.tahlilukclient.fragments.HomeFragment
import com.project.tahlilukclient.fragments.ProfileFragment
import com.project.tahlilukclient.fragments.SettingsFragment
import com.project.tahlilukclient.utilities.SupportFunctions
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        setScreenElements()
        getToken()
        setDeviceLanguage()
        setListeners()
    }

    private fun setListeners() {
        binding.bottomNavigation.setOnShowListener {
            var fragment: Fragment? = null
            when (it.id) {
                1 -> {
                    fragment = SettingsFragment()
                }
                2 -> {
                    fragment = HomeFragment()
                }
                3 -> {
                    fragment = ProfileFragment()
                }
            }
            loadFragment(fragment)
        }
        //set notification count
        //binding.bottomNavigation.setCount(3, "10")
        //set home fragment initially selected
        binding.bottomNavigation.show(2, true)
        binding.bottomNavigation.setOnClickMenuListener {
            //showToast("click ${it.id}")
        }
        binding.bottomNavigation.setOnReselectListener {
            return@setOnReselectListener
        }
    }

    private fun setScreenElements() {
        binding.bottomNavigation.add(
            MeowBottomNavigation.Model(
                1,
                R.drawable.ic_settings_bottom_nav
            )
        )
        binding.bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_home))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_profile))
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { updateToken(it) }
    }

    private fun updateToken(token: String) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token)
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val documentReference: DocumentReference =
            database.collection(Constants.KEY_COLLECTION_PATIENTS).document(
                preferenceManager.getString(Constants.KEY_PATIENT_ID)!!
            )
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener {
                SupportFunctions.showToast(
                    this,
                    resources.getString(R.string.unable_to_updated_token)
                )
            }
    }

    private fun loadFragment(fragment: Fragment?) {
        //Replace fragments
        supportFragmentManager
            .beginTransaction()
            .replace(binding.frameLayout.id, fragment!!)
            .commit()
    }

    private fun setDeviceLanguage() {
        if (preferenceManager.getString(Constants.KEY_DEVICE_LANGUAGE) == null) {
            when (Locale.getDefault().displayLanguage) {
                Constants.KEY_LANGUAGE_ENGLISH_SYSTEM -> {
                    preferenceManager.putString(
                        Constants.KEY_DEVICE_LANGUAGE,
                        Constants.KEY_LANGUAGE_ENGLISH_SYSTEM
                    )
                }
                Constants.KEY_LANGUAGE_ARABIC_SYSTEM -> {
                    preferenceManager.putString(
                        Constants.KEY_DEVICE_LANGUAGE,
                        Constants.KEY_LANGUAGE_ARABIC_SYSTEM
                    )
                }
                else -> {
                    preferenceManager.putString(
                        Constants.KEY_DEVICE_LANGUAGE,
                        Constants.KEY_LANGUAGE_ENGLISH_SYSTEM
                    )
                }
            }
        } else {
            val deviceLanguage = preferenceManager.getString(Constants.KEY_DEVICE_LANGUAGE)
            if (deviceLanguage == Constants.KEY_LANGUAGE_ENGLISH_SYSTEM) {
                SupportFunctions.setLocale(Constants.KEY_LANGUAGE_ENGLISH, resources)
            } else if (deviceLanguage == Constants.KEY_LANGUAGE_ARABIC_SYSTEM) {
                SupportFunctions.setLocale(Constants.KEY_LANGUAGE_ARABIC, resources)
            }
        }
    }


}