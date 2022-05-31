package com.project.tahlilukclient.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.project.tahlilukclient.R
import com.project.tahlilukclient.databinding.ItemContainerRecentConversionBinding
import com.project.tahlilukclient.listeners.ConversionListener
import com.project.tahlilukclient.models.ChatMessage
import com.project.tahlilukclient.models.Lab

class RecentConversationsAdapter(
    var chatMessages: ArrayList<ChatMessage>,
    var conversionListener: ConversionListener
) :
    RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>() {

    inner class ConversionViewHolder(private var binding: ItemContainerRecentConversionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: ChatMessage) {
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage!!))
            binding.textLabName.text = chatMessage.conversionName
            binding.textRecentMessage.text = chatMessage.message
            binding.root.setOnClickListener {
                val lab = Lab()
                lab.id = chatMessage.conversionId
                lab.labName = chatMessage.conversionName
                lab.image = chatMessage.conversionImage
                conversionListener.onConversionClicked(lab)
            }
        }
    }

    private fun getConversionImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(
            ItemContainerRecentConversionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.setData(chatMessages[position])
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rv_animation))
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }
}