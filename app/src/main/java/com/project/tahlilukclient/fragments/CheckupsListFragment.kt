package com.project.tahlilukclient.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.tahlilukclient.R

import com.project.tahlilukclient.adapters.CheckupsAdapter

import com.project.tahlilukclient.databinding.FragmentCheckupsListBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.listeners.CheckupsListener
import com.project.tahlilukclient.models.Checkups
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.SupportFunctions

class CheckupsListFragment : Fragment(), CheckupsListener {
    private lateinit var binding: FragmentCheckupsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckupsListBinding.inflate(layoutInflater)
        SupportFunctions.loading(true, null, binding.progressBar)
        FirestoreClass().getCheckups(this, Constants.KEY_COLLECTION_Checkups)

        return binding.root
    }

    fun successCheckupsListFromFireStore(checkupsList: ArrayList<Checkups>) {
        if (checkupsList.isNotEmpty()) {
            SupportFunctions.loading(false, null, binding.progressBar)
            binding.rvAnalytics.visibility = View.VISIBLE
            val adapter = CheckupsAdapter(checkupsList, this)
            binding.rvAnalytics.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAnalytics.adapter = adapter
        }
    }

    companion object {

        fun newInstance(): CheckupsListFragment {
            val fragment = CheckupsListFragment()

            return fragment
        }
    }

    override fun checkupItem(item: Checkups) {
        val checkupsDetailsFragment = CheckupsDetailsFragment.newInstance()
        val bundle = Bundle()
        bundle.putSerializable(Constants.ITEM, item)
        checkupsDetailsFragment.arguments = bundle
        val fragmentManager: FragmentManager =
            (binding.root.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.fui_slide_in_right,
            R.anim.fragmentanimation,
            R.anim.fui_slide_in_right,
            R.anim.fragmentanimation
        )
        fragmentTransaction.replace(R.id.fragment_container, checkupsDetailsFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}