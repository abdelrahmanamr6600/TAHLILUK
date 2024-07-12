package com.project.tahlilukclient.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ActivityCheckupsBinding
import com.project.tahlilukclient.fragments.CheckupsListFragment

class CheckupsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCheckupsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckupsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(CheckupsListFragment.newInstance())
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


    private fun setListeners(){
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
    }
}