package com.project.tahlilukclient.activities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ActivityReserveBinding
import com.project.tahlilukclient.fragments.ReserveLabsFragment
import com.project.tahlilukclient.listeners.ChangeStepView

class ReserveActivity : AppCompatActivity(),
    ChangeStepView {
    private var reserveBinding: ActivityReserveBinding? = null
    private lateinit var list: Array<String>
     private var reserveProcessCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reserveBinding = ActivityReserveBinding.inflate(layoutInflater)
        setContentView(reserveBinding!!.root)
        stepView()
        listeners()
        replaceFragment(ReserveLabsFragment.newInstance(this))

    }

    private fun stepView(){
        list = arrayOf("choose\nLab", "choose\nAnalysis", "choose\nAddress", "confirm\nrequest")
        reserveBinding!!.stepView.setLabels(list)
            .setBarColorIndicator(ContextCompat.getColor(this, R.color.third_text))
            .setProgressColorIndicator(ContextCompat.getColor(this, R.color.primary_dark))
            .setLabelColorIndicator(ContextCompat.getColor(this, R.color.primary))
            .drawView()
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = this.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.isAddToBackStackAllowed
        fragmentTransaction.commit()
    }


    private fun listeners(){
        reserveBinding!!.imageBack.setOnClickListener{
            onBackPressed()
        }
    }

    override fun increaseProgress() {
        reserveProcessCounter++
        reserveBinding!!.stepView.setCompletedPosition(reserveProcessCounter).drawView()
    }

    override fun progressDecrease() {
        reserveProcessCounter--
        if (reserveProcessCounter<0){
            reserveProcessCounter=0
        }
        reserveBinding!!.stepView.setCompletedPosition(reserveProcessCounter).drawView()
    }
}