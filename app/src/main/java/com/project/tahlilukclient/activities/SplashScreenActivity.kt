package com.project.tahlilukclient.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ActivitySplashScreenBinding
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.project.tahlilukclient.utilities.SupportFunctions
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private var parentJob = Job()

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)


        val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

        coroutineScope.launch {
            setAnimation()
        }
        coroutineScope.launch {
            setDarkModeState()
        }
        coroutineScope.launch {
            delay(3800)
            moveToSignIn()

        }

    }

    private suspend fun setDarkModeState() {
        val darkModeState = preferenceManager.getString(Constants.KEY_DARK_MODE_STATE)
        if (darkModeState == Constants.KEY_DARK_MODE) {
            SupportFunctions.startNightMode()
        } else {
            SupportFunctions.startLightMode()
        }
    }

    // Animation
    private suspend fun setAnimation() {
        val appName: Animation = AnimationUtils.loadAnimation(this, R.anim.app_name)
        val description: Animation = AnimationUtils.loadAnimation(this, R.anim.description)
        binding.tvDescription.startAnimation(description)
        binding.tvAppName.startAnimation(appName)


    }

    //Start Next Activity AfterSplashScreen
    private suspend fun moveToSignIn() {
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            startMainActivity()
        } else {
            startSignInActivity()
        }
        finish()
    }

    private suspend fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

    }

    private suspend fun startSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel()
    }

}