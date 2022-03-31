package com.project.tahlilukclient.firebase

import android.app.Activity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.project.tahlilukclient.activities.*
import com.project.tahlilukclient.fragments.ChangePasswordFragment
import com.project.tahlilukclient.fragments.ChangePhoneFragment
import com.project.tahlilukclient.fragments.ProfileFragment
import com.project.tahlilukclient.fragments.ReserveLabsFragment
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.Constants
import java.util.*
import kotlin.collections.HashMap

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var documentReference: DocumentReference

    fun updatePatientProfile(
        fragment: Fragment,
        collectionName: String,
        userDocument: String?,
        patient: HashMap<String, Any>
    ) {

        if (userDocument != null) {
            mFireStore.collection(collectionName)
                .document(userDocument)
                .update(patient)
                .addOnSuccessListener {
                    when (fragment) {
                        is ProfileFragment -> {
                            fragment.successfulUpdatePatientProfile()
                        }
                        is ChangePasswordFragment -> {
                            fragment.successfulUpdatePatientPassword()

                        }
                    }
                }
                .addOnFailureListener {
                    when (fragment) {
                        is ProfileFragment -> {
                            fragment.failedUpdatePatientProfile()
                        }
                    }
                }
        }

    }

    fun updateUserPhone(
        fragment: ChangePhoneFragment,
        collectionName: String,
        userDocument: String?,
        patients: HashMap<String, Any>
    ) {
        if (userDocument != null) {
            mFireStore.collection(collectionName)
                .document(userDocument)
                .update(patients)
                .addOnSuccessListener {
                    fragment.successfulUpdateUserPhone()
                }
                .addOnFailureListener {
                    fragment.failedUpdateUserPhone()
                }
        }

    }

    fun checkIfExist(
        activity: SignUpActivity,
        collectionName: String,
        KeyNumber: String,
        inputNumber: String
    ) {
        mFireStore.collection(collectionName)
            .whereEqualTo(KeyNumber, inputNumber)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result!!.documents.size > 0) {
                    activity.setNumberError()

                } else {
                    activity.numberNew()
                }
            }
    }

    fun checkIfExist(
        fragment: ChangePhoneFragment,
        collectionName: String,
        KeyNumber: String,
        inputNumber: String
    ) {
        mFireStore.collection(collectionName)
            .whereEqualTo(
                KeyNumber,
                inputNumber
            )
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result!!.documents.size > 0) {
                    fragment.setNumberError()
                } else {
                    fragment.numberNew()
                }
            }
    }

    fun checkIfExist(
        activity: ForgotPasswordActivity,
        collectionName: String,
        KeyNumber: String,
        inputNumber: String
    ) {
        mFireStore.collection(collectionName)
            .whereEqualTo(KeyNumber, inputNumber)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result!!.documents.size > 0) {
                    activity.numberExist()

                } else {
                    activity.numberNotExist()
                }
            }
    }

    fun signIn(
        activity: SignInActivity,
        collectionName: String,
        KeyNumber: String,
        inputNumber: String,
        KeyPassword: String,
        inputPassword: String
    ) {
        mFireStore.collection(collectionName)
            .whereEqualTo(KeyNumber, inputNumber)
            .whereEqualTo(KeyPassword, inputPassword)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result!!.documents.size > 0) {
                    activity.successfulSignIn(it)
                } else {
                    activity.unsuccessfulSignIn()
                }
            }
    }

    fun signUp(
        activity: VerifyPhoneNumberActivity,
        collectionName: String,
        patient: HashMap<Any, Any>,
    ) {
        mFireStore.collection(collectionName)
            .add(patient)
            .addOnSuccessListener {
                activity.successfulSignUp(it)
            }
            .addOnFailureListener {
                activity.unsuccessfulSignUp(it)
            }
    }

    fun signInByCredentials(activity: VerifyPhoneNumberActivity, credential: PhoneAuthCredential) {
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                activity
            ) { task ->
                if (task.isSuccessful) {
                    activity.successfulSignInByCredentials()

                } else {
                    activity.unsuccessfulSignInByCredentials(task)
                }
            }
            .addOnFailureListener {
                activity.failureSignInByCredentials(it)
            }
    }

    fun signInByCredentialsChangePhoneFragment(
        fragment: ChangePhoneFragment,
        credential: PhoneAuthCredential
    ) {
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                fragment.requireActivity()
            ) { task ->
                if (task.isSuccessful) {
                    fragment.successfulSignInByCredentials()
                } else {
                    fragment.unsuccessfulSignInByCredentials(task)
                }
            }
            .addOnFailureListener {
                fragment.failedSignInByCredentials(it)
            }
    }

    fun resetPassword(
        activity: ResetPasswordActivity,
        collectionName: String,
        KeyNumber: String,
        phoneNumber: String,
        PatientID: OnCompleteListener<QuerySnapshot>
    ) {
        mFireStore.collection(collectionName)
            .whereEqualTo(KeyNumber, phoneNumber)
            .get()
            .addOnCompleteListener(PatientID)
            .addOnFailureListener {
                activity.failureResetPassword()
            }
    }

    fun updateConversion(
        activity: ResetPasswordActivity,
        collectionName: String,
        KeyPassword: String,
        newPassword: String,
        conversionId: String
    ) {
        documentReference = mFireStore.collection(collectionName).document(conversionId)
        documentReference.update(
            KeyPassword,
            newPassword,
        )
            .addOnSuccessListener {
                activity.successfulUpdateConversion()
            }
            .addOnFailureListener {
                activity.unsuccessfulUpdateConversion()
            }
    }

    fun listenConversations(
        collectionName: String,
        senderIdKey: String,
        patientIdKey: String,
        receiverIdKey: String,
        eventListener: EventListener<QuerySnapshot>
    ) {
        mFireStore.collection(collectionName)
            .whereEqualTo(
                senderIdKey,
                patientIdKey
            )
            .addSnapshotListener(eventListener)

        mFireStore.collection(collectionName)
            .whereEqualTo(
                receiverIdKey,
                patientIdKey
            )
            .addSnapshotListener(eventListener)
    }

    fun sendMessage(
        activity: ChatActivity,
        collectionName: String,
        message: HashMap<Any, Any>,
        conversionId: String?,
        isReceiverAvailable: Boolean
    ) {
        mFireStore.collection(collectionName).add(message)
        if (conversionId != null) {
            activity.goUpdateConversion()

        } else {
            activity.goAddConversion()

        }
        if (!isReceiverAvailable) {
            activity.goSendNotification()
        }
    }

    fun listenAvailabilityOfReceiver(
        activity: ChatActivity,
        collectionName: String,
        receiverLab: Lab
    ) {
        mFireStore.collection(collectionName).document(
            receiverLab.id!!
        ).addSnapshotListener(
            activity
        ) { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (value != null) {
                activity.receiverAvailable(value)
            }
        }
    }

    fun listenMessages(
        collectionName: String,
        senderIdKey: String,
        patientIdKey: String,
        receiverIdKey: String,
        receiverLab: Lab,
        eventListener: EventListener<QuerySnapshot>
    ) {
        mFireStore.collection(collectionName)
            .whereEqualTo(
                senderIdKey,
                patientIdKey
            )
            .whereEqualTo(receiverIdKey, receiverLab.id)
            .addSnapshotListener(eventListener)
        mFireStore.collection(collectionName)
            .whereEqualTo(senderIdKey, receiverLab.id)
            .whereEqualTo(
                receiverIdKey,
                patientIdKey
            )
            .addSnapshotListener(eventListener)
    }

    fun addConversion(
        activity: ChatActivity,
        collectionName: String,
        conversion: HashMap<String, Any>
    ) {
        mFireStore.collection(collectionName)
            .add(conversion)
            .addOnSuccessListener {
                activity.successfulAddConversion(it)
            }
    }

    fun updateConversion(
        collectionName: String,
        conversionId: String,
        lastMessageKey: String,
        message: String,
        timeStampKey: String
    ) {
        documentReference =
            mFireStore.collection(collectionName).document(conversionId)
        documentReference.update(
            lastMessageKey,
            message,
            timeStampKey,
            Date()
        )
    }

    fun checkForConversionRemotely(
        collectionName: String,
        senderIdKey: String,
        senderId: String,
        receiverIdKey: String,
        receiverId: String,
        conversionOnCompleteListener: OnCompleteListener<QuerySnapshot>
    ) {
        mFireStore.collection(collectionName)
            .whereEqualTo(senderIdKey, senderId)
            .whereEqualTo(receiverIdKey, receiverId)
            .get()
            .addOnCompleteListener(conversionOnCompleteListener)
    }

    fun signOut(
        fragment: ProfileFragment,
        collectionName: String,
        patientIdKey: String,
        tokenKey: String
    ) {
        documentReference =
            mFireStore.collection(collectionName).document(
                patientIdKey
            )
        val updates: HashMap<String, Any> = HashMap()
        updates[tokenKey] = FieldValue.delete()
        documentReference.update(updates)
            .addOnSuccessListener {
                fragment.successfulSignOut()
            }
            .addOnFailureListener {
                fragment.failedSignOut()
            }
    }

//    fun getLabs2(activity: Activity,collectionName: String){
//        //getLabsList FromFireStore
//        mFireStore.collection(Constants.Key_COLLECTION_LABS)
//            .get().addOnSuccessListener { document ->
//                val labsList:ArrayList<Lab> =ArrayList()
//                for (labObject in document.documents){
//                    //convert every documents to object that type lab and pass it to List
//                    val lab = labObject.toObject(Lab::class.java)
//                    if (lab != null) {
//                        lab.id = labObject.id
//                        labsList.add(lab)
//                        when(activity){
//                            is ReserveActivity ->{
//                                activity.placeMarkerOnMap(labsList)
//                            }
//                        }
//                    }
//                }
//
//                when(activity){
//                    is LabsActivity ->{
//                        activity.successLabFromFireStore(labsList)
//                    }
//                    is LabsChatActivity ->{
//                        activity.successLabFromFireStore(labsList)
//                    }
//                    is ReserveActivity ->{
//                            activity.successLabFromFireStore(labsList)
//                    }
//
//                }
//            }
//            .addOnFailureListener {
//                when(activity){
//                    is LabsActivity ->{
//                        activity.closeProgressBar()
//                    }
//                    is LabsChatActivity ->{
//                        activity.closeProgressBar()
//                    }
//                }
//            }
//    }


    fun getLabs(activity: Activity, collectionName: String) {
        mFireStore.collection(collectionName)
            .get().addOnSuccessListener { document ->
                val labsList: ArrayList<Lab> = ArrayList()
                for (labObject in document.documents) {
                    //convert every documents to object that type lab and pass it to List
                    val lab = labObject.toObject(Lab::class.java)
                    if (lab != null) {
                        lab.id = labObject.id
                        labsList.add(lab)
                        when (activity) {
                            is MapActivity -> {
                                activity.placeMarkerOnMap(labsList)
                                activity.successLabFromFireStore(labsList)
                            }
                            is LabsActivity -> {
                                activity.successLabFromFireStore(labsList)
                            }
                            is LabsChatActivity -> {
                                activity.successLabFromFireStore(labsList)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is LabsActivity -> {
                        activity.closeProgressBar()
                    }
                    is LabsChatActivity -> {
                        activity.closeProgressBar()
                    }
                }
            }
    }


    fun getLabs(fragment: Fragment){
        //getLabsList FromFireStore
        mFireStore.collection(Constants.Key_COLLECTION_LABS)
            .get().addOnSuccessListener { document ->
                val labsList:ArrayList<Lab> =ArrayList()
                for (labObject in document.documents){
                    //convert every documents to object that type lab and pass it to List
                    val lab = labObject.toObject(Lab::class.java)
                    if (lab != null) {
                        lab.id = labObject.id
                        labsList.add(lab)
                    }
                }

                when(fragment){
                    is ReserveLabsFragment ->{
                        fragment.successLabFromFireStore(labsList)
                    }
                }
            }
            .addOnFailureListener {
                when(fragment){
                    is ReserveLabsFragment ->{
                        fragment.closeProgressBar()
                    }
                }
            }
    }


}




