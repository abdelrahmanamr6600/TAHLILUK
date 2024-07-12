package com.project.tahlilukclient.fragments

import GetReady
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.tahlilukclient.databinding.FragmentGetReadyDetailsBinding
import com.project.tahlilukclient.utilities.Constants

class GetReadyDetailsFragment : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var getReady:GetReady
    private lateinit var binding :FragmentGetReadyDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle=requireArguments()
        getReady = bundle.getSerializable(Constants.ITEM) as GetReady

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGetReadyDetailsBinding.inflate(layoutInflater)
      setData(getReady)
        return binding.root
    }

    companion object {
        fun newInstance(): GetReadyDetailsFragment {
            return GetReadyDetailsFragment()
        }
    }

    private fun setData(item:GetReady){
        binding.tvTitle.text = item.title
        binding.tvBody.text = item.body
    }
}