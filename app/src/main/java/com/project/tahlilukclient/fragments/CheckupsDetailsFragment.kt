package com.project.tahlilukclient.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.tahlilukclient.databinding.FragmentCheckupsDetailsBinding
import com.project.tahlilukclient.models.Checkups
import com.project.tahlilukclient.utilities.Constants


class CheckupsDetailsFragment : Fragment() {
    private lateinit var binding:FragmentCheckupsDetailsBinding
    private lateinit var bundle:Bundle
    private lateinit var checkups:Checkups

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle=requireArguments()
        checkups = bundle.getSerializable(Constants.ITEM) as Checkups

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckupsDetailsBinding.inflate(layoutInflater)
       setData(checkups)
        return binding.root
    }

    companion object {

        fun newInstance(): CheckupsDetailsFragment {
            return CheckupsDetailsFragment()
        }
    }

    private fun setData(item:Checkups){
        binding.tvTitle.text = item.title
        binding.tvBody.text = item.body
    }
}