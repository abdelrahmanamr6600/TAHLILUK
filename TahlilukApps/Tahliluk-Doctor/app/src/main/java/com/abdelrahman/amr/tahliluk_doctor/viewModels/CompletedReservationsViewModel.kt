package com.abdelrahman.amr.tahliluk_doctor.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.abdelrahman.amr.tahliluk_doctor.models.Patient
import com.abdelrahman.amr.tahliluk_doctor.models.Reserve
import com.abdelrahman.amr.tahliluk_doctor.repositories.CompletedReservationsRepository
import com.abdelrahman.amr.tahliluk_doctor.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.ArrayList

class CompletedReservationsViewModel:ViewModel() {
    private var mCompletedReservationsRepository=CompletedReservationsRepository()

    suspend fun getReservations(orderDate:String,context: Context): MutableStateFlow<ArrayList<Reserve>> {
        return mCompletedReservationsRepository.getReservations(orderDate,context)
    }

    suspend fun getPatientInfo(patientId:String):MutableStateFlow<Patient>{
        return  mCompletedReservationsRepository.getPatientInfo(Constants.KEY_COLLECTION_PATIENTS,patientId)
    }
}