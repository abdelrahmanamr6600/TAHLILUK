package com.project.tahlilukclient.activities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ActivityReserveBinding
import com.project.tahlilukclient.fragments.RequestReserveAnalyticsFragment
import com.project.tahlilukclient.fragments.ReserveLabsFragment
import com.project.tahlilukclient.listeners.ChangeStepView
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.Constants

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
        if(intent.hasExtra(Constants.KEY_LAB))
        {
            val lab = intent.getSerializableExtra(Constants.KEY_LAB) as Lab
            reserveProcessCounter = 1
            reserveBinding!!.stepView.setCompletedPosition(reserveProcessCounter).drawView()
            val requestReserveAnalyticsFragment =RequestReserveAnalyticsFragment.newInstance(this)
            val bundle = Bundle()
            bundle.putSerializable("lab",lab)
            requestReserveAnalyticsFragment.arguments = bundle
            val fragmentManager: FragmentManager =
                (reserveBinding!!.root.context as FragmentActivity).supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fui_slide_in_right,R.anim.fragmentanimation,R.anim.fui_slide_in_right,R.anim.fragmentanimation)
            fragmentTransaction.replace(R.id.fragment_container, requestReserveAnalyticsFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        else{
            replaceFragment(ReserveLabsFragment.newInstance(this))
        }





    }

    private fun stepView(){
        list = arrayOf(resources.getString(R.string.choose_lab), resources.getString(R.string.choose_analytics), resources.getString(R.string.choose_Address), resources.getString(R.string.confirm_Request))
        reserveBinding!!.stepView.setLabels(list)
            .setBarColorIndicator(ContextCompat.getColor(this, R.color.BarColorIndicator))
            .setProgressColorIndicator(ContextCompat.getColor(this, R.color.primary))
            .setLabelColorIndicator(ContextCompat.getColor(this, R.color.primary_text))
            .drawView()
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = this.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.fui_slide_in_right,R.anim.fragmentanimation,R.anim.fui_slide_in_right,R.anim.fragmentanimation)
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

    override fun onBackPressed() {
        progressDecrease()
        super.onBackPressed()
    }
}