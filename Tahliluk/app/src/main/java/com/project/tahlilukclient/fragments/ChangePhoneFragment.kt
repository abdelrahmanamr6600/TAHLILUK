package com.project.tahlilukclient.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.FragmentChangePhoneBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.listeners.OnChangePhoneFragmentReturnListener
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.project.tahlilukclient.utilities.SupportFunctions
import java.util.concurrent.TimeUnit


class ChangePhoneFragment : BottomSheetDialogFragment() {
    private lateinit var changePhoneBinding: FragmentChangePhoneBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var auth: FirebaseAuth
    private var verificationCodeBySystem: String? = null
    private var newNumber: String? = null
    private val patients = HashMap<String, Any>()
    lateinit var listenerChangePhone: OnChangePhoneFragmentReturnListener


    override fun onCreate(savedInstanceState: Bundle?) {
        changePhoneBinding = FragmentChangePhoneBinding.inflate(layoutInflater)
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changePhoneBinding = FragmentChangePhoneBinding.inflate(inflater)
        return changePhoneBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
        preferenceManager = PreferenceManager(requireContext())
        return super.onCreateDialog(savedInstanceState)
    }

    companion object {
        var TAG = "changePhone"

        @JvmStatic
        fun newInstance(listenerChangePhone: OnChangePhoneFragmentReturnListener) =
            ChangePhoneFragment().apply {
                this.listenerChangePhone = listenerChangePhone
                arguments = Bundle().apply {
                }
            }
    }

    fun setListeners() {
        changePhoneBinding.btnChangePhone.setOnClickListener {
            if (isValidChangePasswordDetails()) {
                checkIfExist()
            }
        }
        changePhoneBinding.refreshCodeInput.setOnClickListener {
            refreshCodeInput()
        }
        changePhoneBinding.btnVerifyNewPhoneNumber.setOnClickListener {
            val code = changePhoneBinding.codeInputView.code
            if (code.isEmpty() || code.length < 6) {
                changePhoneBinding.codeInputView.error = resources.getString(R.string.wrong_otp)
                changePhoneBinding.codeInputView.requestFocus()
                return@setOnClickListener
            }
            changePhoneBinding.progressBar.visibility = View.VISIBLE
            verifyCode(code)

        }

    }

//


    private fun sendVerificationCodeToUser(phoneNo: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+20$phoneNo")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
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

        private fun verifyCode(codeByUser: String) {
            if (verificationCodeBySystem != null) {
                val credential: PhoneAuthCredential =
                    PhoneAuthProvider.getCredential(verificationCodeBySystem!!, codeByUser)
                signInByCredentials(credential)

            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.wrong_code),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //if the sim card in the device
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            if (code != null) {
                verifyCode(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun signInByCredentials(credential: PhoneAuthCredential) {
        FirestoreClass().signInByCredentialsChangePhoneFragment(this, credential)
    }

    fun successfulSignInByCredentials() {
        patients[Constants.KEY_PHONE_NUMBER] = newNumber.toString()
        FirestoreClass().updateUserPhone(
            this,
            Constants.KEY_COLLECTION_PATIENTS,
            preferenceManager.getString(Constants.KEY_PATIENT_ID),
            patients
        )
    }

    fun unsuccessfulSignInByCredentials(task: Task<AuthResult>) {
        SupportFunctions.showToast(
            requireContext(),
            task.exception!!.message.toString()
        )
    }

    fun failedSignInByCredentials(ex: Exception) {
        SupportFunctions.showToast(requireContext(), ex.message.toString())
    }

    fun successfulUpdateUserPhone() {
        preferenceManager.putString(
            Constants.KEY_PHONE_NUMBER,
            patients[Constants.KEY_PHONE_NUMBER].toString()
        )
        listenerChangePhone.onChangePhoneFragmentReturn(patients[Constants.KEY_PHONE_NUMBER].toString())
        dismiss()
    }

    fun failedUpdateUserPhone() {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.can_not_update_information),
            Toast.LENGTH_LONG
        ).show()
    }


    private fun isValidChangePasswordDetails(): Boolean {
        if (changePhoneBinding.etNewNumber.text.toString().trim().isEmpty()) {
            changePhoneBinding.etNewNumber.error = resources.getString(R.string.enter_phone_number)
            changePhoneBinding.etNewNumber.requestFocus()
            return false
        } else if (changePhoneBinding.etNewNumber.text.toString().trim().length != 11) {
            changePhoneBinding.etNewNumber.error = resources.getString(R.string.must_be_11_digits)
            changePhoneBinding.etNewNumber.requestFocus()
            return false
        } else {
            return true
        }
    }


    private fun refreshCodeInput() {
        changePhoneBinding.codeInputView.error = ""
        changePhoneBinding.codeInputView.clearError()
        changePhoneBinding.codeInputView.code = ""
        changePhoneBinding.codeInputView.setEditable(true)
    }

    private fun checkIfExist() {
        SupportFunctions.loading(
            true,
            changePhoneBinding.btnChangePhone,
            changePhoneBinding.progressBar
        )
        FirestoreClass().checkIfExist(
            this,
            Constants.KEY_COLLECTION_PATIENTS,
            Constants.KEY_PHONE_NUMBER,
            changePhoneBinding.etNewNumber.text.toString()
        )
    }

    fun setNumberError() {
        changePhoneBinding.etNewNumber.error =
            resources.getString(R.string.this_phone_number_already_have_an_account)
        changePhoneBinding.etNewNumber.requestFocus()
        SupportFunctions.loading(
            false,
            changePhoneBinding.btnChangePhone,
            changePhoneBinding.progressBar
        )
    }

    fun numberNew() {
        SupportFunctions.loading(
            false,
            changePhoneBinding.btnChangePhone,
            changePhoneBinding.progressBar
        )
        goForVerification()
    }

    private fun goForVerification() {
        changePhoneBinding.changeNumber.visibility = View.GONE
        changePhoneBinding.verifyCode.visibility = View.VISIBLE
        newNumber = changePhoneBinding.etNewNumber.text.toString()
        sendVerificationCodeToUser(newNumber!!)
    }

    private fun verifyCode(codeByUser: String) {
        SupportFunctions.loading(
            true,
            changePhoneBinding.btnChangePhone,
            changePhoneBinding.progressBar
        )
        if (verificationCodeBySystem != null) {
            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(verificationCodeBySystem!!, codeByUser)
            signInByCredentials(credential)

        } else {
            SupportFunctions.showToast(requireContext(), resources.getString(R.string.wrong_code))
        }
        SupportFunctions.loading(
            false,
            changePhoneBinding.btnChangePhone,
            changePhoneBinding.progressBar
        )
    }


}


