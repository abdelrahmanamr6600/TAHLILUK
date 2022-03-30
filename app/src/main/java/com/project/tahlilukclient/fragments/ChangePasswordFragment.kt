package com.project.tahlilukclient.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.FragmentChangePasswordBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.listeners.OnChangePasswordFragmentReturnListener
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager


class ChangePasswordFragment : BottomSheetDialogFragment() {
    lateinit var changePasswordBinding: FragmentChangePasswordBinding
    private lateinit var preferenceManager: PreferenceManager
    private val patient = HashMap<String, Any>()
    private var newPassword = " "
    lateinit var listenerChangePassword: OnChangePasswordFragmentReturnListener


    override fun onCreate(savedInstanceState: Bundle?) {
        changePasswordBinding = FragmentChangePasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
        preferenceManager = PreferenceManager(requireContext())
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changePasswordBinding = FragmentChangePasswordBinding.inflate(inflater)
        return changePasswordBinding.root

    }

    companion object {
        var TAG = "changePassword"

        @JvmStatic
        fun newInstance(listenerChangePassword: OnChangePasswordFragmentReturnListener) =
            ChangePasswordFragment().apply {
                this.listenerChangePassword = listenerChangePassword
            }
    }

    fun setListeners() {
        changePasswordBinding.btnChangePassword.setOnClickListener {
            if (isValidChangePasswordDetails()) {
                updatePatientProfile()
            }

        }
    }

    private fun isValidChangePasswordDetails(): Boolean {
        when {
            changePasswordBinding.etOldPassword.text.toString().trim().isEmpty() -> {
                changePasswordBinding.etOldPassword.requestFocus()
                changePasswordBinding.etOldPassword.error = resources.getString(R.string.enter_current_password)
                return false
            }
            changePasswordBinding.etOldPassword.text.toString() != preferenceManager.getString(
                Constants.KEY_PASSWORD
            ) -> {
                changePasswordBinding.etOldPassword.requestFocus()
                changePasswordBinding.etOldPassword.error = resources.getString(R.string.check_current_password)
                return false
            }
            changePasswordBinding.etNewPassword.text.toString().trim().isEmpty() -> {
                changePasswordBinding.etNewPassword.requestFocus()
                changePasswordBinding.etNewPassword.error = resources.getString(R.string.enter_new_password)
                return false
            }
            changePasswordBinding.etNewPassword.text.toString().trim().length < 7 -> {
                changePasswordBinding.etNewPassword.requestFocus()
                changePasswordBinding.etNewPassword.error = resources.getString(R.string.less_than_7_digits)
                return false
            }
            changePasswordBinding.etOldPassword.text.toString().trim().isEmpty() -> {
                changePasswordBinding.etConfirmPassword.requestFocus()
                changePasswordBinding.etConfirmPassword.error = resources.getString(R.string.enter_confirm_password)
                return false
            }
            changePasswordBinding.etConfirmPassword.text.toString() != changePasswordBinding.etNewPassword.text.toString() -> {
                changePasswordBinding.etConfirmPassword.requestFocus()
                changePasswordBinding.etConfirmPassword.error = resources.getString(R.string.not_the_same)
                return false
            }
            else -> {
                return true
            }
        }
    }

    private fun updatePatientProfile() {
        newPassword = changePasswordBinding.etNewPassword.text.toString()
        patient[Constants.KEY_PASSWORD] = newPassword
        FirestoreClass().updatePatientProfile(
            this,
            Constants.KEY_COLLECTION_PATIENTS,
            preferenceManager.getString(Constants.KEY_PATIENT_ID)!!,
            patient
        )
    }

    fun successfulUpdatePatientPassword() {
        preferenceManager.putString(
            Constants.KEY_PASSWORD, patient[Constants.KEY_PASSWORD].toString()
        )
        listenerChangePassword.onChangePasswordFragmentReturn(true)
        dismiss()
    }


}
