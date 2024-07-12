package com.project.tahlilukclient.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.project.tahlilukclient.R
import com.project.tahlilukclient.activities.SplashScreenActivity
import com.project.tahlilukclient.databinding.FragmentSettingsBinding
import com.project.tahlilukclient.databinding.ItemCustomDialogLayoutBinding
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.project.tahlilukclient.utilities.SupportFunctions

class SettingsFragment : Fragment() {
    private lateinit var fragmentSettingsBinding: FragmentSettingsBinding
    private lateinit var itemCustomDialogLayoutBinding: ItemCustomDialogLayoutBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var alert: AlertDialog
    private var englishRadioButtonState: Boolean = false
    private var arabicRadioButtonState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(requireContext())
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater)
        return fragmentSettingsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val darkModeState = preferenceManager.getString(Constants.KEY_DARK_MODE_STATE)
        fragmentSettingsBinding.swDarkMode.isChecked = darkModeState == Constants.KEY_DARK_MODE
        itemCustomDialogLayoutBinding = ItemCustomDialogLayoutBinding.inflate(
            layoutInflater, fragmentSettingsBinding.root, false
        )
        setListeners()
    }


    private fun showDialog() {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        if (itemCustomDialogLayoutBinding.root.parent != null) {
            (itemCustomDialogLayoutBinding.root.parent as ViewGroup).removeView(
                itemCustomDialogLayoutBinding.root
            )
        }
        builder.setView(itemCustomDialogLayoutBinding.root)
        alert = builder.create()

        val deviceLanguage = preferenceManager.getString(Constants.KEY_DEVICE_LANGUAGE)
        if (deviceLanguage == Constants.KEY_LANGUAGE_ENGLISH_SYSTEM) {
            itemCustomDialogLayoutBinding.rbEnglish.isChecked = true
            itemCustomDialogLayoutBinding.rbArabic.isChecked = false
            englishRadioButtonState = true
            arabicRadioButtonState = false
        } else if (deviceLanguage == Constants.KEY_LANGUAGE_ARABIC_SYSTEM) {
            itemCustomDialogLayoutBinding.rbEnglish.isChecked = false
            itemCustomDialogLayoutBinding.rbArabic.isChecked = true
            englishRadioButtonState = false
            arabicRadioButtonState = true
        }

        if (alert.window != null) {
            alert.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alert.show()

    }

    private fun setListeners() {
        fragmentSettingsBinding.btnChangeLanguage.setOnClickListener {
            showDialog()
        }

        fragmentSettingsBinding.swDarkMode.setOnClickListener {
            if (fragmentSettingsBinding.swDarkMode.isChecked) {
                startNightMode()
            } else {
                startLightMode()
            }
        }


        itemCustomDialogLayoutBinding.rbEnglish.setOnClickListener {
            if (englishRadioButtonState && !arabicRadioButtonState) {
                alert.dismiss()
            } else {
                SupportFunctions.setLocale(Constants.KEY_LANGUAGE_ENGLISH, resources)
                englishRadioButtonState = true
                arabicRadioButtonState = false
                preferenceManager.putString(
                    Constants.KEY_DEVICE_LANGUAGE,
                    Constants.KEY_LANGUAGE_ENGLISH_SYSTEM
                )
                alert.dismiss()
                val intent = Intent(requireContext(), SplashScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }


        itemCustomDialogLayoutBinding.rbArabic.setOnClickListener {
            if (arabicRadioButtonState && !englishRadioButtonState) {
                alert.dismiss()
            } else {
                SupportFunctions.setLocale(Constants.KEY_LANGUAGE_ARABIC, resources)
                englishRadioButtonState = false
                arabicRadioButtonState = true
                preferenceManager.putString(
                    Constants.KEY_DEVICE_LANGUAGE,
                    Constants.KEY_LANGUAGE_ARABIC_SYSTEM
                )
                alert.dismiss()
                val intent = Intent(requireContext(), SplashScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

        fragmentSettingsBinding.btnAboutUs.setOnClickListener {
            AboutUsFragment.newInstance()
                .show(childFragmentManager, AboutUsFragment.TAG)

        }

    }

    private fun startNightMode() {
        preferenceManager.putString(Constants.KEY_DARK_MODE_STATE, Constants.KEY_DARK_MODE)
        fragmentSettingsBinding.swDarkMode.isChecked = true
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        SupportFunctions.startNightMode()
    }

    private fun startLightMode() {
        preferenceManager.putString(Constants.KEY_DARK_MODE_STATE, Constants.KEY_LIGHT_MODE)
        fragmentSettingsBinding.swDarkMode.isChecked = false
        SupportFunctions.startLightMode()
    }
}