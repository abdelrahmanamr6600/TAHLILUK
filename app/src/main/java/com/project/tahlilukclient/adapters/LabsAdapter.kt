package com.project.tahlilukclient.adapters
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ItemContainerLabBinding
import com.project.tahlilukclient.listeners.LabListener
import com.project.tahlilukclient.models.Lab

class LabsAdapter :
    RecyclerView.Adapter<LabsAdapter.LabViewHolder> {
    private var labs:ArrayList<Lab> = ArrayList()
    private lateinit var labListener:LabListener
    constructor()

    constructor( labs: ArrayList<Lab>,  listener:LabListener){
        this.labs = labs
        this.labListener = listener

    }

    inner class LabViewHolder(var binding: ItemContainerLabBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setLabData(lab: Lab) {
            binding.textLabName.text = lab.name
            binding.textLabAddress.text = lab.address
            binding.imageProfile.setImageBitmap(getLabImage(lab.image!!))
            binding.root.setOnClickListener {
                labListener.onLabClicked(lab)
            }
        }

    }

    private fun getLabImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabViewHolder {
        val binding =
            ItemContainerLabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabViewHolder, position: Int) {
        holder.setLabData(labs[position])
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rv_animation))


    }

    override fun getItemCount(): Int {
        return labs.size
    }

    fun setFilteredList(FilteredList:ArrayList<Lab>){
        this.labs = FilteredList
        notifyDataSetChanged()
    }


}