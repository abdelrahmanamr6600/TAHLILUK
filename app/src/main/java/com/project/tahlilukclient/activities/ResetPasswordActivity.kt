package com.project.tahlilukclient.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ActivityResetPasswordBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.utilities.SupportFunctions

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityResetPasswordBinding
    private lateinit var preferenceManager: PreferenceManager
    var userPhoneNumber: String? = null
    private var conversionId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        checkInternetConnection()
        getDataFromIntent()
        setListeners()
    }

    private fun getDataFromIntent() {
        userPhoneNumber = intent.getStringExtra(Constants.KEY_PHONE_NUMBER)
    }

    private fun setListeners() {
        binding.btnReset.setOnClickListener {
            if (isValidSignUpDetails()) {
                resetPassword(userPhoneNumber!!)
            }
        }
    }

    private fun isValidSignUpDetails(): Boolean {
        if (binding.inputNewPassword.text.toString().trim().isEmpty()) {
            binding.inputNewPassword.error = resources.getString(R.string.enter_password)
            binding.inputNewPassword.requestFocus()
            return false

        } else if (binding.inputNewPassword.text.toString().trim().length < 7) {
            binding.inputNewPassword.error = resources.getString(R.string.less_than_7_digits)
            binding.inputNewPassword.requestFocus()
            return false
        } else if (binding.confirmNewPassword.text.toString().trim().isEmpty()) {
            binding.confirmNewPassword.error = resources.getString(R.string.enter_confirm_password)
            binding.confirmNewPassword.requestFocus()
            return false
        } else if (binding.inputNewPassword.text.toString() != binding.confirmNewPassword.text.toString()) {
            binding.confirmNewPassword.error = resources.getString(R.string.not_the_same)
            return false
        } else {
            return true
        }
    }


    private fun resetPassword(phoneNumber: String) {
        SupportFunctions.loading(true, binding.btnReset, binding.progressBar)
        FirestoreClass().resetPassword(
            this,
            Constants.KEY_COLLECTION_PATIENTS,
            Constants.KEY_PHONE_NUMBER,
            phoneNumber,
            getPatientID
        )
    }

    fun failureResetPassword() {
        SupportFunctions.loading(false, binding.btnReset, binding.progressBar)
        SupportFunctions.showToast(
            applicationContext,
            resources.getString(R.string.something_wrong)
        )
    }

    private val getPatientID: OnCompleteListener<QuerySnapshot> =
        OnCompleteListener {
            if (it.isSuccessful && it.result != null && it.result!!.documents.size > 0) {
                val documentSnapshot: DocumentSnapshot = it.result!!.documents[0]
                conversionId = documentSnapshot.id
                updateConversion(binding.inputNewPassword.text.toString())
            }
        }

    private fun updateConversion(newPassword: String) {
        FirestoreClass().updateConversion(
            this,
            Constants.KEY_COLLECTION_PATIENTS,
            Constants.KEY_PASSWORD,
            newPassword,
            conversionId!!
        )

    }

    fun successfulUpdateConversion() {
        val intent = Intent(applicationContext, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    fun unsuccessfulUpdateConversion() {
        SupportFunctions.loading(false, binding.btnReset, binding.progressBar)
        SupportFunctions.showToast(applicationContext, resources.getString(R.string.unable_to_reset_your_password))

    }
    private fun checkInternetConnection(){
        if (SupportFunctions.checkForInternet(this))  {

        }
        else
        {
            Toast.makeText(this,"please check you internet connection", Toast.LENGTH_LONG).show()
        }
    }
}