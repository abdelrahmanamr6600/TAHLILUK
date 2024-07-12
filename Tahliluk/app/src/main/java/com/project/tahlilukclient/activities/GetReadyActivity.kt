package com.project.tahlilukclient.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ActivityGetReadyBinding
import com.project.tahlilukclient.fragments.GetReadyListFragment

class GetReadyActivity : AppCompatActivity() {
     private lateinit var binding:ActivityGetReadyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetReadyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(GetReadyListFragment.newInstance())
        setListeners()
    }



    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = this.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.fui_slide_in_right,R.anim.fragmentanimation,R.anim.fui_slide_in_right,R.anim.fragmentanimation)
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.isAddToBackStackAllowed
        fragmentTransaction.commit()
    }


    private fun  setListeners(){
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
    }
}