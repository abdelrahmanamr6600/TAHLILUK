package com.project.tahlilukclient.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.project.tahlilukclient.R
import com.project.tahlilukclient.activities.SplashScreenActivity
import com.project.tahlilukclient.databinding.DialogProgressBinding
import com.project.tahlilukclient.databinding.FragmentProfileBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.listeners.OnChangePasswordFragmentReturnListener
import com.project.tahlilukclient.listeners.OnChangePhoneFragmentReturnListener
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.project.tahlilukclient.utilities.SupportFunctions
import java.io.FileNotFoundException
import java.io.InputStream
import kotlin.collections.HashMap

class ProfileFragment : Fragment(), OnChangePasswordFragmentReturnListener,
    OnChangePhoneFragmentReturnListener {
    private lateinit var fragmentProfileBinding: FragmentProfileBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var binding: DialogProgressBinding
    private var encodedImage: String? = null
    private val patients = HashMap<String, Any>()
    private var EDIT_STATE = 0


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater)
        binding = DialogProgressBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPatientDetails()
        setListener()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater)
        return fragmentProfileBinding.root
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setListener() {
        fragmentProfileBinding.ivUserImageProfileActivity.setOnClickListener {
            if (EDIT_STATE != 0) {
                getImage()
            }
        }
        fragmentProfileBinding.btnEdit.setOnClickListener {
            if (EDIT_STATE == 1) {
                if (isValidSignUpDetails()) {
                    updatePatientProfile()
                }
            } else {
                switchEditButton()
            }
        }

        fragmentProfileBinding.tvChangePassword.setOnClickListener {
            ChangePasswordFragment.newInstance(this)
                .show(childFragmentManager, ChangePasswordFragment.TAG)
        }
        fragmentProfileBinding.btnChangePhone.setOnClickListener {
            ChangePhoneFragment.newInstance(this)
                .show(childFragmentManager, ChangePhoneFragment.TAG)
        }
        fragmentProfileBinding.btnLogout.setOnClickListener {
            signOut()
        }


    }

    private fun loadPatientDetails() {
        fragmentProfileBinding.firstName.setText(preferenceManager.getString(Constants.KEY_FIRSTNAME))
        fragmentProfileBinding.lastName.setText(preferenceManager.getString(Constants.KEY_LASTNAME))
        fragmentProfileBinding.btnChangePhone.text =
            preferenceManager.getString(Constants.KEY_PHONE_NUMBER)
        val gender = preferenceManager.getString(Constants.KEY_GENDER)
        if (gender == Constants.KEY_MALE) {
            fragmentProfileBinding.rbMale.isChecked = true
        } else {
            fragmentProfileBinding.rbFemale.isChecked = true
        }
        fragmentProfileBinding.ivUserImageProfileActivity.setImageBitmap(
            SupportFunctions.decodeImage(preferenceManager.getString(Constants.KEY_IMAGE)!!)
        )
    }

    private fun switchEditButton() {

        fragmentProfileBinding.firstName.isEnabled = true
        fragmentProfileBinding.lastName.isEnabled = true
        fragmentProfileBinding.rbMale.isEnabled = true
        fragmentProfileBinding.rbFemale.isEnabled = true
        fragmentProfileBinding.firstName.backgroundTintMode = null
        fragmentProfileBinding.lastName.backgroundTintMode = null
        fragmentProfileBinding.rbMale.backgroundTintMode = null
        fragmentProfileBinding.rbFemale.backgroundTintMode = null
        fragmentProfileBinding.btnEdit.setText(R.string.save)

        EDIT_STATE = 1
    }

    private fun updatePatientProfile() {
        SupportFunctions.showProgressBar(
            requireContext(),
            resources.getString(R.string.please_wait),
            binding.tvProgressText
        )
        if (encodedImage == null) {
            encodedImage = preferenceManager.getString(Constants.KEY_IMAGE)
        }
        patients[Constants.KEY_IMAGE] = encodedImage!!
        patients[Constants.KEY_FIRSTNAME] = fragmentProfileBinding.firstName.text.toString()
        patients[Constants.KEY_LASTNAME] = fragmentProfileBinding.lastName.text.toString()
        val gender = if (fragmentProfileBinding.rbMale.isChecked) {
            Constants.KEY_MALE
        } else {
            Constants.KEY_FEMALE
        }
        patients[Constants.KEY_GENDER] = gender

        FirestoreClass().updatePatientProfile(
            this,
            Constants.KEY_COLLECTION_PATIENTS,
            preferenceManager.getString(Constants.KEY_PATIENT_ID),
            patients
        )
    }

    fun successfulUpdatePatientProfile(
    ) {
        SupportFunctions.hideDialog()
        preferenceManager.putString(
            Constants.KEY_FIRSTNAME,
            patients[Constants.KEY_FIRSTNAME].toString()
        )
        preferenceManager.putString(
            Constants.KEY_LASTNAME,
            patients[Constants.KEY_LASTNAME].toString()
        )
        preferenceManager.putString(
            Constants.KEY_GENDER,
            patients[Constants.KEY_GENDER].toString()
        )
        preferenceManager.putString(
            Constants.KEY_IMAGE,
            patients[Constants.KEY_IMAGE].toString()
        )
    }

    fun failedUpdatePatientProfile() {
        SupportFunctions.hideDialog()
        SupportFunctions.showToast(
            requireContext(),
            resources.getString(R.string.can_not_update_information)
        )
    }


    private fun encodedImage(bitmap: Bitmap): String {
        return SupportFunctions.encodedImage(bitmap)
    }


    private val pickImage: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val imageUri: Uri? = result.data!!.data
                    try {
                        val inputStream: InputStream? =
                            activity?.contentResolver?.openInputStream(imageUri!!)
                        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                        fragmentProfileBinding.ivUserImageProfileActivity.setImageBitmap(bitmap)
                        encodedImage = encodedImage(bitmap)

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private fun isValidSignUpDetails(): Boolean {
        if (encodedImage == null && preferenceManager.getString(Constants.KEY_IMAGE) == null) {
            SupportFunctions.showToast(
                requireContext(),
                resources.getString(R.string.select_profile_image)
            )
            return false
        } else if (fragmentProfileBinding.firstName.text.toString().trim().isEmpty()) {
            fragmentProfileBinding.firstName.error = resources.getString(R.string.enter_first_name)
            fragmentProfileBinding.firstName.requestFocus()
            return false
        } else if (fragmentProfileBinding.lastName.text.toString().trim().isEmpty()) {
            fragmentProfileBinding.lastName.error = resources.getString(R.string.enter_last_name)
            fragmentProfileBinding.lastName.requestFocus()
            return false
        } else if (fragmentProfileBinding.rdGender.checkedRadioButtonId == -1) {
            SupportFunctions.showToast(
                requireContext(),
                resources.getString(R.string.please_select_your_gender)
            )
            fragmentProfileBinding.rdGender.requestFocus()
            return false
        } else {
            return true
        }
    }

    private fun getImage() {
        SupportFunctions.getImage(pickImage)
    }

    private fun signOut() {
        SupportFunctions.loading(
            true,
            fragmentProfileBinding.btnLogout,
            fragmentProfileBinding.progressBar
        )
        FirestoreClass().signOut(
            this,
            Constants.KEY_COLLECTION_PATIENTS,
            preferenceManager.getString(Constants.KEY_PATIENT_ID)!!,
            Constants.KEY_FCM_TOKEN
        )
    }


    fun successfulSignOut() {
        val deviceLanguage = preferenceManager.getString(Constants.KEY_DEVICE_LANGUAGE)
        val darkModeState = preferenceManager.getString(Constants.KEY_DARK_MODE_STATE)
        preferenceManager.clear()
        if (deviceLanguage == Constants.KEY_LANGUAGE_ENGLISH_SYSTEM) {
            preferenceManager.putString(
                Constants.KEY_DEVICE_LANGUAGE,
                Constants.KEY_LANGUAGE_ENGLISH_SYSTEM
            )
        } else if (deviceLanguage == Constants.KEY_LANGUAGE_ARABIC_SYSTEM) {
            preferenceManager.putString(
                Constants.KEY_DEVICE_LANGUAGE,
                Constants.KEY_LANGUAGE_ARABIC_SYSTEM
            )
        }
        if (darkModeState == Constants.KEY_DARK_MODE) {
            preferenceManager.putString(Constants.KEY_DARK_MODE_STATE, Constants.KEY_DARK_MODE)
        } else {
            preferenceManager.putString(Constants.KEY_DARK_MODE_STATE, Constants.KEY_LIGHT_MODE)
        }
        val intent = Intent(requireContext(), SplashScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    fun failedSignOut() {
        SupportFunctions.showToast(
            requireContext(),
            resources.getString(R.string.unable_to_sign_out)
        )
    }

    override fun onChangePasswordFragmentReturn(status: Boolean) {
        if (status) {
            signOut()
        }
    }

    override fun onChangePhoneFragmentReturn(newNumber: String) {
        fragmentProfileBinding.btnChangePhone.text = newNumber
    }



}


