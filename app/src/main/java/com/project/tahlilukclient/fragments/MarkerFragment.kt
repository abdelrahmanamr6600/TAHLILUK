package com.project.tahlilukclient.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.tahlilukclient.R
import com.project.tahlilukclient.activities.ChatActivity
import com.project.tahlilukclient.activities.ReserveActivity
import com.project.tahlilukclient.databinding.FragmentMarkerBinding
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.Constants

class MarkerFragment : BottomSheetDialogFragment() {
    private lateinit var markerFragmentBinding: FragmentMarkerBinding
    private  var lab :Lab = Lab()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        markerFragmentBinding = FragmentMarkerBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        markerFragmentBinding.textLabName.text = lab.name
        markerFragmentBinding = FragmentMarkerBinding.inflate(inflater)
        return markerFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillInLabData()
        setListeners()
    }

    companion object {
     var Tag = "tag"
        @JvmStatic
        fun newInstance(Lab :Lab) =
            MarkerFragment().apply {
                lab = Lab
                arguments = Bundle().apply {


                }
            }
    }

    private fun setListeners(){
        markerFragmentBinding.ivOpenChat.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra(Constants.KEY_LAB, lab)
            startActivity(intent)
            activity?.finish()
        }

        markerFragmentBinding.ivReqReserve.setOnClickListener {
            val intent = Intent(requireContext(), ReserveActivity::class.java)
            intent.putExtra(Constants.KEY_LAB, lab)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun  fillInLabData(){
        markerFragmentBinding.textLabName.text = lab.name
        markerFragmentBinding.textLabAddress.text = lab.address
        markerFragmentBinding.ivLabProfile.setImageBitmap(getLabImage(lab.image!!))
    }

    private fun getLabImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}