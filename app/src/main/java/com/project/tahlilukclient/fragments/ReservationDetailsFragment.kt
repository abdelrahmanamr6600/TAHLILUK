package com.project.tahlilukclient.fragments
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.project.tahlilukclient.R
import com.project.tahlilukclient.adapters.ConfirmAnalyticsAdapter
import com.project.tahlilukclient.databinding.DialogProgressBinding
import com.project.tahlilukclient.databinding.FragmentReservationDetailsBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.models.Reserve
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.SupportFunctions
import java.io.File

class ReservationDetailsFragment : Fragment() {
    private lateinit var reservationDetailsBinding: FragmentReservationDetailsBinding
    private lateinit var bundle: Bundle
    private lateinit var reservation:Reserve
    private  lateinit var adapter: ConfirmAnalyticsAdapter
    private lateinit var image:String
    private lateinit var labName:String
    private lateinit var bindingDialog: DialogProgressBinding
    companion object {
        @JvmStatic
        fun newInstance() =
            ReservationDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         bundle=requireArguments()
        reservation = bundle.getSerializable(Constants.Reservation) as Reserve
        image = bundle.getString(Constants.IMAGE)!!
        labName = bundle.getString(Constants.LAB_NAME)!!
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        reservationDetailsBinding = FragmentReservationDetailsBinding.inflate(layoutInflater)
        bindingDialog = DialogProgressBinding.inflate(layoutInflater)
        setReservationData()
        setListeners()
        return reservationDetailsBinding.root
    }
    private fun setReservationData(){
       reservationDetailsBinding.tvOrderLabName.text = labName
        reservationDetailsBinding.tvOrderDate.text = reservation.orderDateTime
        reservationDetailsBinding.tvCheckoutSubTotal.text = reservation.orderAnalyticsPrice
        reservationDetailsBinding.tvCheckoutTotalAmount.text=reservation.orderTotalAmount
        reservationDetailsBinding.tvConfirmAdditionalNote.text=reservation.orderAdditionalInformation
        reservationDetailsBinding.tvConfirmAddress.text = reservation.orderAddress
        reservationDetailsBinding.tvCheckoutShippingCharge.text =getString(R.string.visit_price)
        when(reservation.orderState){
            getString(R.string.pending)  -> reservationDetailsBinding.tvOrderState.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.error_color))
            getString(R.string.inprogress)  -> reservationDetailsBinding.tvOrderState.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.error))
            getString(R.string.completed) -> reservationDetailsBinding.tvOrderState.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.primary_text))
        }
        reservationDetailsBinding.tvOrderState.text = reservation.orderState
        reservationDetailsBinding.ivLabImage.setImageBitmap(getLabImage(image))
        adapter = ConfirmAnalyticsAdapter(reservation.analyticsList!!)
        reservationDetailsBinding.rvCartListItems.layoutManager = LinearLayoutManager(requireContext())
        reservationDetailsBinding.rvCartListItems.adapter = adapter
        if (reservation.orderState==getString(R.string.pending)||reservation.orderState==getString(R.string.pending)){
            reservationDetailsBinding.CvResult.visibility =View.GONE
        }
        else
        {
            reservationDetailsBinding.btnCancelRequest.visibility = View.GONE
            reservationDetailsBinding.CvResult.visibility =View.VISIBLE


        }
    }
private fun setListeners(){
    reservationDetailsBinding.btnCancelRequest.setOnClickListener {
        FirestoreClass().deleteReservation(this,reservation.orderId!!)
        requireActivity().onBackPressed()
    }

    reservationDetailsBinding.btnShowResult.setOnClickListener {
        SupportFunctions.showProgressBar(
            requireContext(),
            resources.getString(R.string.please_wait),
            bindingDialog.tvProgressText
        )
        val url = reservation.results!!
        val fileName = reservation.orderDateTime!!
        downloadPdfFromInternet(
            url,
            getRootDirPath(requireContext()),
            fileName
        )
    }

}
    private fun getLabImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }



    private fun downloadPdfFromInternet(url: String, dirPath: String, fileName: String) {
        PRDownloader.initialize(requireContext())
        PRDownloader.download(
            url,
            dirPath,
            fileName
        ).build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    SupportFunctions.hideDialog()
                    val downloadedFile = File(dirPath, fileName)
                    val imageResultFragment = ResultFragment.newInstance(downloadedFile)
                    val bundle = Bundle()
                    bundle.putString(Constants.RESULT_IMAGE, image)
                    bundle.putString(Constants.LAB_NAME,labName)
                    imageResultFragment.arguments = bundle
                    val fragmentManager: FragmentManager =
                        (reservationDetailsBinding.root.context as FragmentActivity).supportFragmentManager
                    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.setCustomAnimations(
                        R.anim.fui_slide_in_right,
                        R.anim.fragmentanimation,
                        R.anim.fui_slide_in_right,
                        R.anim.fragmentanimation
                    )
                    fragmentTransaction.replace(R.id.fragment_container, imageResultFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }

                override fun onError(error: com.downloader.Error?) {

                }

            })
    }

    private fun getRootDirPath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file: File = ContextCompat.getExternalFilesDirs(
                context.applicationContext,
                null
            )[0]
            file.absolutePath
        } else {
            context.applicationContext.filesDir.absolutePath
        }
    }

}