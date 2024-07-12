package com.project.tahlilukclient.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.tahlilukclient.activities.MainActivity
import com.project.tahlilukclient.databinding.FragmentFinishReservationBinding
import kotlinx.coroutines.*

class FinishReservationFragment : Fragment() {
    private lateinit var finishReservationBinding: FragmentFinishReservationBinding
    private lateinit var bundle :Bundle
    private var parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        finishReservationBinding = FragmentFinishReservationBinding.inflate(inflater)
        bundle = requireArguments()
        var phone = bundle.getString("phone")
        finishReservationBinding.tvPhone.text = phone

        coroutineScope.launch {
            delay(2000)
            requireActivity().finish()

        }
        return finishReservationBinding.root
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            FinishReservationFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}