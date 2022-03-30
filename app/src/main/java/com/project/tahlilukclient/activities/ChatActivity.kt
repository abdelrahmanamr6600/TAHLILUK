package com.project.tahlilukclient.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.project.tahlilukclient.R
import com.project.tahlilukclient.adapters.ChatAdapter
import com.project.tahlilukclient.databinding.ActivityChatBinding
import com.project.tahlilukclient.firebase.FirestoreClass
import com.project.tahlilukclient.models.ChatMessage
import com.project.tahlilukclient.models.Lab
import com.project.tahlilukclient.network.ApiClient
import com.project.tahlilukclient.network.ApiService
import com.project.tahlilukclient.utilities.Constants
import com.project.tahlilukclient.utilities.PreferenceManager
import com.project.tahlilukclient.utilities.SupportFunctions
import org.jetbrains.annotations.NotNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : BaseActivity() {
    lateinit var binding: ActivityChatBinding
    private lateinit var receiverLab: Lab
    private lateinit var chatMessages: ArrayList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    lateinit var preferenceManager: PreferenceManager
    private var conversionId: String? = null
    private var isReceiverAvailable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        loadReceiverDetails()
        init()
        listenMessages()
    }

    private fun init() {
        preferenceManager = PreferenceManager(applicationContext)
        chatMessages = ArrayList()
        chatAdapter = ChatAdapter(
            preferenceManager.getString(Constants.KEY_PATIENT_ID)!!,
            chatMessages,
            getBitmapFromEncodedString(receiverLab.image)
        )
        binding.chatRecyclerView.adapter = chatAdapter
    }

    private fun loadReceiverDetails() {
        receiverLab = intent.getSerializableExtra(Constants.KEY_LAB) as Lab
        binding.textName.text = receiverLab.name
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
        binding.layoutSend.setOnClickListener {
            if (binding.inputMessage.text.isNotEmpty()) {
                    sendMessage()

            }
        }
    }


    private fun sendMessage() {
        val message: HashMap<Any, Any> = HashMap()
        message[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_PATIENT_ID)!!
        message[Constants.KEY_RECEIVER_ID] = receiverLab.id!!
        message[Constants.KEY_MESSAGE] = binding.inputMessage.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()

        FirestoreClass().sendMessage(
            this,
            Constants.KEY_COLLECTION_CHAT,
            message,
            conversionId,
            isReceiverAvailable
        )
        binding.inputMessage.text = null
    }

    fun goUpdateConversion() {
        updateConversion(binding.inputMessage.text.toString())
    }

    fun goAddConversion() {
        val conversion: HashMap<String, Any> = HashMap()
        conversion[Constants.KEY_SENDER_ID] =
            preferenceManager.getString(Constants.KEY_PATIENT_ID)!!
        conversion[Constants.KEY_SENDER_NAME] =
            preferenceManager.getString(Constants.KEY_FIRSTNAME)!!
        conversion[Constants.KEY_SENDER_IMAGE] = preferenceManager.getString(Constants.KEY_IMAGE)!!
        conversion[Constants.KEY_RECEIVER_ID] = receiverLab.id!!
        conversion[Constants.KEY_RECEIVER_NAME] = receiverLab.name!!
        conversion[Constants.KEY_RECEIVER_IMAGE] = receiverLab.image!!
        conversion[Constants.KEY_LAST_MESSAGE] = binding.inputMessage.text.toString()
        conversion[Constants.KEY_TIMESTAMP] = Date()
        addConversion(conversion)
    }

    fun goSendNotification() {
        try {
            val tokens = JSONArray()
            tokens.put(receiverLab.token)
            val data = JSONObject()
            data.put(
                Constants.KEY_PATIENT_ID,
                preferenceManager.getString(Constants.KEY_PATIENT_ID)
            )
            data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_FIRSTNAME))
            data.put(
                Constants.KEY_FCM_TOKEN,
                preferenceManager.getString(Constants.KEY_FCM_TOKEN)
            )
            data.put(Constants.KEY_MESSAGE, binding.inputMessage.text.toString())

            val body = JSONObject()
            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendNotification(body.toString())
        } catch (exception: Exception) {
            SupportFunctions.showToast(applicationContext, exception.message!!.toString())
        }
    }

    private fun sendNotification(messageBody: String) {
        ApiClient.getClient()?.create(ApiService::class.java)?.sendMessage(
            Constants.getRemoteMsgHeaders(),
            messageBody
        )?.enqueue(object : Callback<String> {
            override fun onResponse(
                @NotNull call: Call<String>,
                @NotNull response: Response<String>
            ) {
                if (response.isSuccessful) {
                    try {
                        if (response.body() != null) {
                            val responseJson = JSONObject(response.body()!!)
                            val results: JSONArray = responseJson.getJSONArray("results")
                            if (responseJson.getInt("failure") == 1) {
                                val error: JSONObject = results.get(0) as JSONObject
                                SupportFunctions.showToast(
                                    applicationContext,
                                    error.getString("error")
                                )
                                return
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    SupportFunctions.showToast(applicationContext, "Notification sent successfully")
                } else {
                    //SupportFunctions.showToast(applicationContext, "Error: ${response.code()}")
                    SupportFunctions.showToast(
                        applicationContext,
                        resources.getString(R.string.error) + " " + response.code()
                    )
                }
            }

            override fun onFailure(@NotNull call: Call<String>, @NotNull t: Throwable) {
                //SupportFunctions.showToast(applicationContext, t.toString())
                SupportFunctions.showToast(applicationContext, resources.getString(R.string.error) + " " + t)
            }

        })
    }

    private fun listenAvailabilityOfReceiver() {
        FirestoreClass().listenAvailabilityOfReceiver(
            this,
            Constants.KEY_COLLECTION_PATIENTS,
            receiverLab
        )
    }

    fun receiverAvailable(value: DocumentSnapshot) {
        if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
            val availability: Int = Objects.requireNonNull(
                value.getLong(Constants.KEY_AVAILABILITY)
            )!!.toInt()
            isReceiverAvailable = availability == 1
        }
        receiverLab.token = value.getString(Constants.KEY_FCM_TOKEN)
        if (receiverLab.image == null) {
            receiverLab.image = value.getString(Constants.KEY_IMAGE)
            chatAdapter.setReceiverImageProfile(getBitmapFromEncodedString(receiverLab.image)!!)
            chatAdapter.notifyItemChanged(0, chatMessages.size)
        }
        if (isReceiverAvailable) {
            binding.textAvailability.visibility = View.VISIBLE
        } else {
            binding.textAvailability.visibility = View.GONE
        }
    }

    private fun listenMessages() {
        FirestoreClass().listenMessages(
            Constants.KEY_COLLECTION_CHAT,
            Constants.KEY_SENDER_ID,
            preferenceManager.getString(Constants.KEY_PATIENT_ID)!!,
            Constants.KEY_RECEIVER_ID,
            receiverLab,
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
                val count = chatMessages.size
                for (documentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val chatMessage = ChatMessage()
                        chatMessage.senderId =
                            documentChange.document.getString(Constants.KEY_SENDER_ID)
                        chatMessage.receiverId =
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        chatMessage.message =
                            documentChange.document.getString(Constants.KEY_MESSAGE)
                        chatMessage.dateTime =
                            getReadableDateTime(documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!)
                        chatMessage.dateObject =
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                        chatMessages.add(chatMessage)
                    }

                }

                chatMessages.sortWith { obj1: ChatMessage, obj2: ChatMessage ->
                    obj1.dateObject!!.compareTo(obj2.dateObject)
                }
                if (count == 0) {
                    chatAdapter.notifyDataSetChanged()
                } else {
                    chatAdapter.notifyItemRangeInserted(chatMessages.size, chatMessages.size)
                    binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
                }
                binding.chatRecyclerView.visibility = View.VISIBLE
            }
            SupportFunctions.loading(false, null, binding.progressBar)
            if (conversionId == null) {
                checkForConversion()
            }
        }


    private fun getReadableDateTime(date: Date): String {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

    private fun getBitmapFromEncodedString(encodedImage: String?): Bitmap? {
        return if (encodedImage != null) {
            return SupportFunctions.decodeImage(encodedImage)
        } else {
            null
        }
    }

    private fun addConversion(conversion: HashMap<String, Any>) {
        FirestoreClass().addConversion(this, Constants.KEY_COLLECTION_CONVERSATIONS, conversion)
    }

    fun successfulAddConversion(dF: DocumentReference) {
        conversionId = dF.id
    }

    private fun updateConversion(message: String) {
        FirestoreClass().updateConversion(
            Constants.KEY_COLLECTION_CONVERSATIONS,
            conversionId!!,
            Constants.KEY_LAST_MESSAGE,
            message,
            Constants.KEY_TIMESTAMP
        )
    }

    private fun checkForConversion() {
        if (chatMessages.size != 0) {
            checkForConversionRemotely(
                preferenceManager.getString(Constants.KEY_PATIENT_ID)!!,
                receiverLab.id!!
            )
            checkForConversionRemotely(
                receiverLab.id!!,
                preferenceManager.getString(Constants.KEY_PATIENT_ID)!!
            )
        }
    }

    private fun checkForConversionRemotely(senderId: String, receiverId: String) {
        FirestoreClass().checkForConversionRemotely(
            Constants.KEY_COLLECTION_CONVERSATIONS,
            Constants.KEY_SENDER_ID,
            senderId,
            Constants.KEY_RECEIVER_ID,
            receiverId,
            conversionOnCompleteListener
        )
    }

    private val conversionOnCompleteListener: OnCompleteListener<QuerySnapshot> =
        OnCompleteListener {
            if (it.isSuccessful && it.result != null && it.result!!.documents.size > 0) {
                val documentSnapshot: DocumentSnapshot = it.result!!.documents[0]
                conversionId = documentSnapshot.id
            }

        }

    override fun onResume() {
        super.onResume()
        listenAvailabilityOfReceiver()
    }

}