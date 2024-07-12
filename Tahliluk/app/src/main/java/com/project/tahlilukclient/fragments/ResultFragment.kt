package com.project.tahlilukclient.fragments

import android.annotation.SuppressLint

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.tahlilukclient.databinding.FragmentResultBinding
import com.project.tahlilukclient.utilities.Constants
import java.io.File

class ResultFragment : Fragment() {
    lateinit var binding: FragmentResultBinding
    private lateinit var bundle: Bundle
    private lateinit var labName: String
    private lateinit var   downloadedFile :File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = requireArguments()
        labName = bundle.getString(Constants.LAB_NAME)!!

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(layoutInflater)
        binding.pdf.fromFile(downloadedFile)
            .password(null)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .onPageError { page, _ ->

            }
            .load()
        binding.tvLabName.text = labName

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(File:File) =
            ResultFragment().apply {
                downloadedFile = File
              Log.d("path", downloadedFile.path)

                arguments = Bundle().apply {

                }
            }
    }
}


