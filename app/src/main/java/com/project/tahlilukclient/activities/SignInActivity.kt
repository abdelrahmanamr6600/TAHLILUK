package com.project.tahlilukclient.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.google.firebase.firestore.QuerySnapshot
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ActivitySignInBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.utilities.SupportFunctions

class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        setListeners()

    }




    private fun setListeners() {
        binding.textSignUp.setOnClickListener {
            startSignUpActivity()
        }
        binding.forgot.setOnClickListener {
            startForgotPasswordActivity()
        }
        binding.btnSignIn.setOnClickListener {
            if (isValidSignInDetails()) {
                SupportFunctions.loading(true, binding.btnSignIn, binding.progressBar)
                FirestoreClass().signIn(
                    this,
                    Constants.KEY_COLLECTION_PATIENTS,
                    Constants.KEY_PHONE_NUMBER,
                    binding.inputPhoneNumber.text.toString(),
                    Constants.KEY_PASSWORD,
                    binding.inputPassword.text.toString()
                )
            }
        }
    }



    fun successfulSignIn(task: Task<QuerySnapshot>) {
        val documentSnapshot = task.result!!.documents[0]
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
        preferenceManager.putString(Constants.KEY_PATIENT_ID, documentSnapshot.id)
        preferenceManager.putString(
            Constants.KEY_FIRSTNAME, documentSnapshot.getString(
                Constants.KEY_FIRSTNAME
            )!!
        )
        preferenceManager.putString(
            Constants.KEY_LASTNAME, documentSnapshot.getString(
                Constants.KEY_LASTNAME
            )!!
        )
        preferenceManager.putString(
            Constants.KEY_IMAGE, documentSnapshot.getString(
                Constants.KEY_IMAGE
            )!!
        )
        preferenceManager.putString(
            Constants.KEY_PHONE_NUMBER, documentSnapshot.getString(
                Constants.KEY_PHONE_NUMBER
            )!!
        )
        preferenceManager.putString(
            Constants.KEY_GENDER, documentSnapshot.getString(
                Constants.KEY_GENDER
            )!!
        )
        preferenceManager.putString(
            Constants.KEY_PASSWORD, documentSnapshot.getString(
                Constants.KEY_PASSWORD
            )!!
        )
        startMainActivityWithFlags()
    }

    fun unsuccessfulSignIn() {
        SupportFunctions.loading(false, binding.btnSignIn, binding.progressBar)
        SupportFunctions.showToast(
            applicationContext,
            resources.getString(R.string.unable_to_sign_in)
        )
    }


    private fun isValidSignInDetails(): Boolean {
        if (binding.inputPhoneNumber.text.toString().trim().isEmpty()) {
            binding.inputPhoneNumber.error = resources.getString(R.string.enter_phone_number)
            binding.inputPhoneNumber.requestFocus()
            return false
        } else if (binding.inputPhoneNumber.text.toString().trim().length != 11) {
            binding.inputPhoneNumber.error = resources.getString(R.string.must_be_11_digits)
            binding.inputPhoneNumber.requestFocus()
            return false
        } else if (binding.inputPassword.text.toString().trim().isEmpty()) {
            binding.inputPassword.error = resources.getString(R.string.enter_password)
            binding.inputPassword.requestFocus()
            return false
        } else if (binding.inputPassword.text.toString().trim().length < 7) {
            binding.inputPassword.error = resources.getString(R.string.less_than_7_digits)
            binding.inputPassword.requestFocus()
            return false
        } else {
            return true
        }
    }

    private fun startSignUpActivity() {
        startActivity(Intent(applicationContext, SignUpActivity::class.java))
    }

    private fun startForgotPasswordActivity() {
        startActivity(Intent(applicationContext, ForgotPasswordActivity::class.java))
    }



    private fun startMainActivityWithFlags() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }



}

