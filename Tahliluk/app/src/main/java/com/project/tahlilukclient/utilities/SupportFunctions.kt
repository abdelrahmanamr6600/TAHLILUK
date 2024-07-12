package com.project.tahlilukclient.utilities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.project.tahlilukclient.R
import com.project.tahlilukclient.activities.LabsActivity
import com.project.tahlilukclient.databinding.FragmentHomeBinding
import java.io.ByteArrayOutputStream
import java.util.*

class SupportFunctions : AppCompatActivity() {
    companion object {
        private lateinit var mProgressBar: Dialog


        fun showToast(context: Context, message: String) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM, 0, 0)
            toast.show()
        }

        fun loading(isLoading: Boolean, button: View?, progressBar: View) {
            if (button != null) {
                if (isLoading) {
                    button.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.INVISIBLE
                    button.visibility = View.VISIBLE
                }
            } else {
                if (isLoading) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE
                }
            }

        }

        fun getImage(pickImage: ActivityResultLauncher<Intent>) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)

        }

        fun encodedImage(bitmap: Bitmap): String {
            val previewWidth = 150
            val previewHeight = bitmap.height * previewWidth / bitmap.width
            val previewBitmap: Bitmap =
                Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
            val byteArrayOutputStream = ByteArrayOutputStream()
            previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val bytes: ByteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        }

        fun decodeImage(keyImage: String): Bitmap {
            val bytes: ByteArray =
                Base64.decode(keyImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        fun showProgressBar(context: Context, text: String, progressText: TextView) {
            mProgressBar = Dialog(context)
            mProgressBar.setContentView(R.layout.dialog_progress)
            mProgressBar.setCancelable(false)
            mProgressBar.setCanceledOnTouchOutside(false)
            progressText.text = text
            mProgressBar.show()
        }

        fun hideDialog() {
            mProgressBar.dismiss()
        }

        fun setLocale(language: String, resources: Resources) {
            val metrics: DisplayMetrics = resources.displayMetrics
            val configuration: Configuration = resources.configuration
            configuration.setLocale(Locale(language))
            resources.updateConfiguration(configuration, metrics)
        }


        fun startNightMode() {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        fun startLightMode() {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


        fun checkForInternet(context: Context): Boolean {
            // register activity with the connectivity manager service
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            // if the android version is equal to M
            // or greater we need to use the
            // NetworkCapabilities to check what type of
            // network has the internet connection

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false
            // Representation of the capabilities of an active network.
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        }

        fun showNoInternetSnackBar(fragmentHomeBinding: FragmentHomeBinding) {
            val snackBarView = Snackbar.make(
                fragmentHomeBinding.constraintLayout,
                " please check internet connection",
                Snackbar.LENGTH_LONG
            )
            val view = snackBarView.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.BOTTOM
            view.layoutParams = params
            snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            snackBarView.show()
        }


        fun isGpsEnabled(activity: Activity): Boolean {
            val manager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        fun turnOnGps(activity: Activity) {
            val locationRequest: LocationRequest =
                LocationRequest.create()
            locationRequest.priority = PRIORITY_HIGH_ACCURACY
            val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            var response: LocationSettingsResponse? = null
            val result: Task<LocationSettingsResponse> =
                LocationServices.getSettingsClient(activity)
                    .checkLocationSettings(builder.build())
            result.addOnCompleteListener { task ->
                try {
                    response = task.getResult(ApiException::class.java)
                } catch (e: ApiException) {
                    if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            val resolvableApiException: ResolvableApiException =
                                e as ResolvableApiException
                            resolvableApiException.startResolutionForResult(
                                activity,
                                PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e("tag", "Exception is $e")
                        }
                    }
                    if (e.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                    }
                }
            }
        }


        fun getPermission(context: Context) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constants.LOCATION_REQUEST_CODE
                )
                return
            } else if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    Constants.LOCATION_REQUEST_CODE
                )
                return
            } else if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    Constants.LOCATION_REQUEST_CODE
                )
                return
            }


        }

        fun fullScreen(window: Window) {
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        }

        fun showDialog(activity: LabsActivity, state: Boolean) {
            AlertDialog.Builder(activity)
                .setTitle(activity.resources.getString(R.string.location))
                .setMessage(activity.resources.getString(R.string.location_error_message))
                .setPositiveButton(
                    activity.resources.getString(R.string.retry)
                ) { _, _ ->
                    when (state) {
                        true -> {
                            activity.reloadRecyclerView(true)
                        }
                        false -> {
                            activity.clickOnMapShow()
                        }
                    }

                }
                .setNegativeButton(
                    activity.resources.getString(R.string.cancel), null
                )
                .setIcon(R.drawable.ic_show_on_map)
                .show()
        }

        fun showSwitcher(state: Boolean, switchView: View) {
            when (state) {
                true -> {
                    switchView.visibility = View.VISIBLE
                }
                false -> {
                    switchView.visibility = View.GONE
                }
            }
        }
    }

}