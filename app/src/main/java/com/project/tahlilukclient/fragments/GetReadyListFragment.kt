package com.project.tahlilukclient.fragments

import GetReady
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
import com.project.tahlilukclient.adapters.GetReadyAdapter
import com.project.tahlilukclient.databinding.FragmentGetReadyListBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.listeners.GetReadyListener
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.SupportFunctions

class GetReadyListFragment : Fragment() , GetReadyListener {
    private lateinit var binding :FragmentGetReadyListBinding

    private var list = ArrayList<GetReady>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentGetReadyListBinding.inflate(layoutInflater)
        SupportFunctions.loading(true,null,binding.progressBar)
        FirestoreClass().getReady(this,Constants.KEY_COLLECTION_GetReady)

        return binding.root
    }

    fun successGetReadyListFromFireStore(getReadyList: ArrayList<GetReady>) {
        if (getReadyList.isNotEmpty()){
            SupportFunctions.loading(false,null,binding.progressBar)
            binding.rvAnalytics.visibility =View.VISIBLE
            val adapter = GetReadyAdapter(getReadyList,this)
            Log.d("s",list.size.toString())
            binding.rvAnalytics.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
            binding.rvAnalytics.adapter = adapter
        }


    }

    companion object {
        fun newInstance(): GetReadyListFragment {
            return GetReadyListFragment()
        }
    }

    override fun getReadyItem(item: GetReady) {
            val getReadyDetailsFragment  = GetReadyDetailsFragment.newInstance()
            val bundle = Bundle()
            bundle.putSerializable(Constants.ITEM,item)
        getReadyDetailsFragment.arguments = bundle
            val fragmentManager: FragmentManager =
                (binding.root.context as FragmentActivity).supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.fui_slide_in_right,R.anim.fragmentanimation,R.anim.fui_slide_in_right,R.anim.fragmentanimation)
        fragmentTransaction.replace(R.id.fragment_container, getReadyDetailsFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
    }



}