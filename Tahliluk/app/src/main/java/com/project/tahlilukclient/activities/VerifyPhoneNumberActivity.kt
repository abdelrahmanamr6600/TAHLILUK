package com.project.tahlilukclient.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.ktx.Firebase
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ActivityVerifyPhoneNumberBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.project.tahlilukclient.utilities.SupportFunctions
import java.util.*
import java.util.concurrent.TimeUnit

class VerifyPhoneNumberActivity : AppCompatActivity() {
    lateinit var binding: ActivityVerifyPhoneNumberBinding
    lateinit var preferenceManager: PreferenceManager
    private lateinit var auth: FirebaseAuth
    private var userImage: String? = null
    private var userFirstName: String? = null
    private var userLastName: String? = null
    private var userPhoneNumber: String? = null
    private var userPassword: String? = null
    private var lunchState: String? = null
    private var verificationCodeBySystem: String? = null
    private var userGender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyPhoneNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        auth = Firebase.auth

        getDataFromIntent()
        setListeners()

    }

    private fun getDataFromIntent() {
        userImage = intent.getStringExtra(Constants.KEY_IMAGE)
        userFirstName = intent.getStringExtra(Constants.KEY_FIRSTNAME)
        userLastName = intent.getStringExtra(Constants.KEY_LASTNAME)
        userPhoneNumber = intent.getStringExtra(Constants.KEY_PHONE_NUMBER)
        userPassword = intent.getStringExtra(Constants.KEY_PASSWORD)
        lunchState = intent.getStringExtra(Constants.KEY_LUNCH_STATE)
        userGender = intent.getStringExtra(Constants.KEY_GENDER)
        sendVerificationCodeToUser(userPhoneNumber!!)
    }

    private fun setListeners() {
        binding.btnVerify.setOnClickListener {
            val code = binding.codeInputView.code
            if (code.isEmpty() || code.length < 6) {
                binding.codeInputView.error = resources.getString(R.string.wrong_otp)
                binding.codeInputView.requestFocus()
                return@setOnClickListener
            }
            SupportFunctions.loading(true, null, binding.progressBar)
            verifyCode(code)

        }
        binding.refreshCodeInput.setOnClickListener {
            refreshCodeInput()
        }


    }

    private fun sendVerificationCodeToUser(phoneNo: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+20$phoneNo")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private val mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object :
        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        //if the sim card not in the device
        override fun onCodeSent(s: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, p1)
            verificationCodeBySystem = s
        }

        //if the sim card in the device
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            if (code != null) {
                SupportFunctions.loading(true, binding.btnVerify, binding.progressBar)
                verifyCode(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            SupportFunctions.showToast(applicationContext, e.message!!.toString())
        }
    }

    private fun verifyCode(codeByUser: String) {
        if (verificationCodeBySystem != null) {
            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(verificationCodeBySystem!!, codeByUser)
            signInByCredentials(credential)
        } else {
            SupportFunctions.showToast(applicationContext, resources.getString(R.string.wrong_code))
        }
    }

    private fun signInByCredentials(credential: PhoneAuthCredential) {
        FirestoreClass().signInByCredentials(this, credential)
    }

    fun successfulSignInByCredentials() {
        if (lunchState == Constants.KEY_LUNCH_STATE_FIRST_TIME) {
            signUp()
        } else if (lunchState == Constants.KEY_LUNCH_STATE_FORGOT_PASSWORD) {
            goToResetPassword()
        }
    }

    fun unsuccessfulSignInByCredentials(task: Task<AuthResult>) {
        SupportFunctions.showToast(
            applicationContext,
            task.exception!!.message.toString()
        )
    }

    fun failureSignInByCredentials(ex: Exception) {
        SupportFunctions.showToast(applicationContext, ex.message.toString())
    }

    private fun signUp() {
        SupportFunctions.loading(true, binding.btnVerify, binding.progressBar)
        val patient: HashMap<Any, Any> = HashMap()
        patient[Constants.KEY_FIRSTNAME] = userFirstName!!
        patient[Constants.KEY_LASTNAME] = userLastName!!
        patient[Constants.KEY_PHONE_NUMBER] = userPhoneNumber!!
        patient[Constants.KEY_PASSWORD] = userPassword!!
        patient[Constants.KEY_IMAGE] = userImage!!
        patient[Constants.KEY_GENDER] = userGender!!

        FirestoreClass().signUp(this, Constants.KEY_COLLECTION_PATIENTS, patient)

    }

    fun successfulSignUp(dR: DocumentReference) {
        SupportFunctions.loading(false, binding.btnVerify, binding.progressBar)
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
        preferenceManager.putString(Constants.KEY_PATIENT_ID, dR.id)
        preferenceManager.putString(Constants.KEY_FIRSTNAME, userFirstName!!)
        preferenceManager.putString(Constants.KEY_LASTNAME, userLastName!!)
        preferenceManager.putString(Constants.KEY_IMAGE, userImage!!)
        preferenceManager.putString(Constants.KEY_PHONE_NUMBER, userPhoneNumber!!)
        preferenceManager.putString(Constants.KEY_GENDER, userGender!!)
        preferenceManager.putString(Constants.KEY_PASSWORD, userPassword!!)
        startMainActivityWithFlags()
    }

    fun unsuccessfulSignUp(ex: Exception) {
        SupportFunctions.loading(false, binding.btnVerify, binding.progressBar)
        SupportFunctions.showToast(applicationContext, ex.message.toString())
    }

    private fun goToResetPassword() {
        val intent = Intent(applicationContext, ResetPasswordActivity::class.java)
        intent.putExtra(Constants.KEY_PHONE_NUMBER, userPhoneNumber)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }


    private fun refreshCodeInput() {
        binding.codeInputView.error = ""
        binding.codeInputView.clearError()
        binding.codeInputView.code = ""
        binding.codeInputView.setEditable(true)
    }

    private fun startMainActivityWithFlags() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

}

