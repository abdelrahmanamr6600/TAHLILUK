package com.project.tahlilukclient.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.project.tahlilukclient.adapters.RecentConversationsAdapter
import com.project.tahlilukclient.databinding.ActivityMainChatBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.listeners.ConversionListener
import com.project.tahlilukclient.models.ChatMessage
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.project.tahlilukclient.utilities.SupportFunctions

class MainChatActivity : BaseActivity(), ConversionListener {
    lateinit var binding: ActivityMainChatBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var conversions: ArrayList<ChatMessage>
    private lateinit var conversationsAdapter: RecentConversationsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        init()
        loadUserDetails()
        setListeners()
        listenConversations()

    }

    private fun init() {
        conversions = ArrayList()
        conversationsAdapter = RecentConversationsAdapter(conversions, this)
        binding.conversationsRecyclerView.adapter = conversationsAdapter
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
        binding.fabNewChat.setOnClickListener {
            startLabsActivity()
        }
    }

    private fun loadUserDetails() {
        binding.textName.text = preferenceManager.getString(Constants.KEY_FIRSTNAME)
        val bitmap =
            SupportFunctions.decodeImage(preferenceManager.getString(Constants.KEY_IMAGE)!!)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun listenConversations() {
        FirestoreClass().listenConversations(
            Constants.KEY_COLLECTION_CONVERSATIONS,
            Constants.KEY_SENDER_ID,
            preferenceManager.getString(Constants.KEY_PATIENT_ID)!!,
            Constants.KEY_RECEIVER_ID,
            eventListener
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private val eventListener: EventListener<QuerySnapshot> =
        EventListener<QuerySnapshot> { value, error ->
            if (error != null) {
                return@EventListener
            }
            if (value != null) {
                for (documentChange: DocumentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val senderId: String =
                            documentChange.document.getString(Constants.KEY_SENDER_ID)!!
                        val receiverId: String =
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID)!!
                        val chatMessage = ChatMessage()
                        chatMessage.senderId = senderId
                        chatMessage.receiverId = receiverId
                        if (preferenceManager.getString(Constants.KEY_PATIENT_ID)
                                .equals(senderId)
                        ) {
                            chatMessage.conversionImage =
                                documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)
                            chatMessage.conversionName =
                                documentChange.document.getString(Constants.KEY_RECEIVER_NAME)
                            chatMessage.conversionId =
                                documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        } else {
                            chatMessage.conversionImage =
                                documentChange.document.getString(Constants.KEY_SENDER_IMAGE)
                            chatMessage.conversionName =
                                documentChange.document.getString(Constants.KEY_SENDER_NAME)
                            chatMessage.conversionId =
                                documentChange.document.getString(Constants.KEY_SENDER_ID)
                        }
                        chatMessage.message =
                            documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                        chatMessage.dateObject =
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                        conversions.add(chatMessage)
                    } else if (documentChange.type == DocumentChange.Type.MODIFIED) {
                        for (i in 0..conversions.size) {
                            val senderId: String =
                                documentChange.document.getString(Constants.KEY_SENDER_ID)!!
                            val receiverId: String =
                                documentChange.document.getString(Constants.KEY_RECEIVER_ID)!!
                            if (conversions[i].senderId.equals(senderId) && conversions[i].receiverId.equals(
                                    receiverId
                                )
                            ) {
                                conversions[i].message =
                                    documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                                conversions[i].dateObject =
                                    documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                                break
                            }
                        }
                    }
                }
                conversions.sortWith { obj1: ChatMessage, obj2: ChatMessage ->
                    obj2.dateObject!!.compareTo(obj1.dateObject)
                }
                conversationsAdapter.notifyDataSetChanged()
                binding.conversationsRecyclerView.smoothScrollToPosition(0)
                binding.conversationsRecyclerView.visibility = View.VISIBLE
                SupportFunctions.loading(false, null, binding.progressBar)
            }
        }

    override fun onConversionClicked(lab: Lab) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_LAB, lab)
        startActivity(intent)
    }

    private fun startLabsActivity() {
        startActivity(Intent(applicationContext, LabsChatActivity::class.java))
    }

}