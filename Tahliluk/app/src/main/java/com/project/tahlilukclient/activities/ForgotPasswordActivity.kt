package com.project.tahlilukclient.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.tahlilukclient.R
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.databinding.ActivityForgotPasswordBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.utilities.SupportFunctions

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setListeners()


    }

    private fun setListeners() {
        binding.textSignIn.setOnClickListener {
            onBackPressed()
        }
        binding.btnSignIn.setOnClickListener {
            if (isValidSignUpDetails()) {
                checkIfExist()
            }
        }
    }

    private fun isValidSignUpDetails(): Boolean {
        if (binding.inputPhoneNumber.text.toString().trim().isEmpty()) {
            binding.inputPhoneNumber.error = resources.getString(R.string.enter_phone_number)
            binding.inputPhoneNumber.requestFocus()
            return false
        } else if (binding.inputPhoneNumber.text.toString().trim().length != 11) {
            binding.inputPhoneNumber.error = resources.getString(R.string.must_be_11_digits)
            binding.inputPhoneNumber.requestFocus()
            return false
        } else {
            return true
        }
    }

    private fun checkIfExist() {
        SupportFunctions.loading(true, binding.btnSignIn, binding.progressBar)
        FirestoreClass().checkIfExist(
            this,
            Constants.KEY_COLLECTION_PATIENTS,
            Constants.KEY_PHONE_NUMBER,
            binding.inputPhoneNumber.text.toString()
        )
    }

    fun numberExist() {
        goForVerification()
        SupportFunctions.loading(false, binding.btnSignIn, binding.progressBar)
    }

    fun numberNotExist() {
        SupportFunctions.loading(false, binding.btnSignIn, binding.progressBar)
        binding.inputPhoneNumber.error = resources.getString(R.string.this_phone_number_does_not_have_an_account)
        binding.inputPhoneNumber.requestFocus()
    }


    private fun goForVerification() {
        val intent = Intent(this, VerifyPhoneNumberActivity::class.java)
        intent.putExtra(Constants.KEY_PHONE_NUMBER, binding.inputPhoneNumber.text.toString())
        intent.putExtra(Constants.KEY_LUNCH_STATE, Constants.KEY_LUNCH_STATE_FORGOT_PASSWORD)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

}