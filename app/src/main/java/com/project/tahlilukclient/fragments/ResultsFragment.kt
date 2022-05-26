package com.project.tahlilukclient.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.FragmentResultsBinding


class ResultsFragment : Fragment() {
    private lateinit var binding :FragmentResultsBinding
    private lateinit var bundle:Bundle
    private lateinit var image:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = requireArguments()
       image = bundle.getString("labImage").toString()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentResultsBinding.inflate(layoutInflater)


        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ResultsFragment().apply {
                }
            }


    private fun getLabImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}