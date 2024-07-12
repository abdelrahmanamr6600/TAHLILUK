package com.project.tahlilukclient.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.project.tahlilukclient.R
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.databinding.ActivitySignUpBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.utilities.SupportFunctions
import java.io.FileNotFoundException
import java.io.InputStream

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var encodedImage: String? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setListeners() {
        binding.textSignIn.setOnClickListener {
            onBackPressed()
        }
        binding.btnSignUp.setOnClickListener {
            if (isValidSignUpDetails()) {
                checkIfExist()
            }
        }
        binding.imageProfile.setOnClickListener {
            getImage()
        }

    }

    private fun getImage() {
        SupportFunctions.getImage(pickImage)
    }

    private val pickImage: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (result.data != null) {
                    val imageUri: Uri? = result.data!!.data
                    try {
                        val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.imageProfile.setImageBitmap(bitmap)
                        encodedImage = SupportFunctions.encodedImage(bitmap)

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }


    private fun isValidSignUpDetails(): Boolean {
        if (encodedImage == null) {
            SupportFunctions.showToast(
                applicationContext,
                resources.getString(R.string.select_profile_image)
            )
            return false
        } else if (binding.firstName.text.toString().trim().isEmpty()) {
            binding.firstName.error = resources.getString(R.string.enter_first_name)
            binding.firstName.requestFocus()
            return false
        } else if (binding.lastName.text.toString().trim().isEmpty()) {
            binding.lastName.error = resources.getString(R.string.enter_last_name)
            binding.lastName.requestFocus()
            return false
        } else if (binding.inputPhoneNumber.text.toString().trim().isEmpty()) {
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
        } else if (binding.confirmPassword.text.toString().trim().isEmpty()) {
            binding.confirmPassword.error = resources.getString(R.string.enter_confirm_password)
            binding.confirmPassword.requestFocus()
            return false
        } else if (binding.inputPassword.text.toString() != binding.confirmPassword.text.toString()) {
            binding.confirmPassword.error = resources.getString(R.string.not_the_same)
            return false
        } else if (binding.rdGender.checkedRadioButtonId == -1) {
            SupportFunctions.showToast(
                applicationContext,
                resources.getString(R.string.please_select_your_gender)
            )
            binding.rdGender.requestFocus()
            return false
        } else {
            return true
        }
    }

    private fun checkIfExist() {
        SupportFunctions.loading(true, binding.btnSignUp, binding.progressBar)
        FirestoreClass().checkIfExist(
            this,
            Constants.KEY_COLLECTION_PATIENTS,
            Constants.KEY_PHONE_NUMBER,
            binding.inputPhoneNumber.text.toString()
        )
    }

    fun setNumberError() {
        binding.inputPhoneNumber.error = resources.getString(R.string.this_phone_number_already_have_an_account)
        binding.inputPhoneNumber.requestFocus()
        SupportFunctions.loading(false, binding.btnSignUp, binding.progressBar)
    }

    fun numberNew() {
        SupportFunctions.loading(false, binding.btnSignUp, binding.progressBar)
        goForVerification()
    }

    private fun goForVerification() {
        val intent = Intent(this, VerifyPhoneNumberActivity::class.java)
        intent.putExtra(Constants.KEY_IMAGE, encodedImage!!)
        intent.putExtra(Constants.KEY_FIRSTNAME, binding.firstName.text.toString())
        intent.putExtra(Constants.KEY_LASTNAME, binding.lastName.text.toString())
        intent.putExtra(Constants.KEY_PHONE_NUMBER, binding.inputPhoneNumber.text.toString())
        intent.putExtra(Constants.KEY_PASSWORD, binding.inputPassword.text.toString())
        intent.putExtra(Constants.KEY_LUNCH_STATE, Constants.KEY_LUNCH_STATE_FIRST_TIME)
        val gender = if (binding.rbMale.isChecked) {
            Constants.KEY_MALE
        } else {
            Constants.KEY_FEMALE
        }
        intent.putExtra(Constants.KEY_GENDER, gender)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }






}