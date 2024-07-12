package com.project.tahlilukclient.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager

open class BaseActivity : AppCompatActivity() {

    private lateinit var documentReference: DocumentReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferenceManager = PreferenceManager(applicationContext)
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        documentReference = database.collection(Constants.KEY_COLLECTION_PATIENTS)
            .document(preferenceManager.getString(Constants.KEY_PATIENT_ID)!!)
    }

    override fun onPause() {
        super.onPause()
        documentReference.update(Constants.KEY_AVAILABILITY, 0)
    }


    override fun onResume() {
        super.onResume()
        documentReference.update(Constants.KEY_AVAILABILITY, 1)

    }
}