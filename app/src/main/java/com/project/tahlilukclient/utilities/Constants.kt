package com.project.tahlilukclient.utilities

class Constants {
    companion object {
        const val KEY_COLLECTION_PATIENTS = "patients"
        const val KEY_NAME = "name"
        const val KEY_FIRSTNAME = "firstName"
        const val KEY_LASTNAME = "lastName"
        const val KEY_PHONE_NUMBER = "phoneNumber"
        const val KEY_PASSWORD = "password"
        const val KEY_PREFERENCE_NAME = "chatAppPreference"
        const val KEY_IS_SIGNED_IN = "isSignedIn"
        const val KEY_PATIENT_ID = "patientId"
        const val KEY_IMAGE = "image"
        const val KEY_LUNCH_STATE = "lunchState"
        const val KEY_LUNCH_STATE_FIRST_TIME = "firstTimeState"
        const val KEY_LUNCH_STATE_FORGOT_PASSWORD = "forgotPasswordState"
        const val KEY_FCM_TOKEN = "fcmToken"
        const val KEY_LAB = "lab"
        const val KEY_COLLECTION_CHAT = "chat"
        const val KEY_SENDER_ID = "senderId"
        const val KEY_RECEIVER_ID = "receiverId"
        const val KEY_MESSAGE = "message"
        const val KEY_TIMESTAMP = "timestamp"
        const val KEY_COLLECTION_CONVERSATIONS = "conversations"
        const val KEY_SENDER_NAME = "senderName"
        const val KEY_RECEIVER_NAME = "receiverName"
        const val KEY_SENDER_IMAGE = "senderImage"
        const val KEY_RECEIVER_IMAGE = "receiverImage"
        const val KEY_LAST_MESSAGE = "lastMessage"
        const val KEY_AVAILABILITY = "availability"
        private const val REMOTE_MSG_AUTHORIZATION = "Authorization"
        private const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
        const val REMOTE_MSG_DATA = "data"
        const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"
        const val KEY_GENDER = "gender"
        const val KEY_MALE = "male"
        const val KEY_FEMALE = "female"
        const val KEY_DEVICE_LANGUAGE = "deviceLanguage"
        const val KEY_LANGUAGE_ENGLISH_SYSTEM = "English"
        const val KEY_LANGUAGE_ARABIC_SYSTEM = "العربية"
        const val KEY_LANGUAGE_ENGLISH = "en"
        const val KEY_LANGUAGE_ARABIC = "ar"
        const val LOCATION_REQUEST_CODE = 1
        const val Key_COLLECTION_LABS: String = "labs"
        const val KEY_SELECT_LAB_CHAT = "chat"
        const val KEY_DARK_MODE_STATE = "darkModeState"
        const val KEY_DARK_MODE = "darkMode"
        const val KEY_LIGHT_MODE = "lightMode"
        const val KEY_CURRENT_LATITUDE = "currentLatitude"
        const val KEY_CURRENT_LONGITUDE = "currentLongitude"
        const val KEY_COLLECTION_RESERVATION = "reservations"
        const val KEY_COLLECTION_GetReady = "GetReady"
        const val KEY_COLLECTION_Checkups = "Checkups"

        //bundle keys
        const val SELECTED_LAB = "lab"
        const val SELECTED_ANALYTICS = "selectedAnalyticsList"
        const val SELECTED_ADDRESS_LATITUDE = "addresslatitude"
        const val SELECTED_ADDRESS_LONGITUDE = "addresslongitude"
        const val RESERVATION_NOTE = "note"
        const val ITEM = "item"


        var remoteMsgHeaders: HashMap<String, String>? = null

        @JvmName("getRemoteMsgHeaders1")
        fun getRemoteMsgHeaders(): HashMap<String, String> {
            if (remoteMsgHeaders == null) {
                remoteMsgHeaders = HashMap()
                remoteMsgHeaders!![REMOTE_MSG_AUTHORIZATION] =
                    "key=AAAARQKYzCc:APA91bHL2FAi1ReNqnYL1uSejyg-iAa7jDghbo6rgMnBf36Mf48lgIJ6gYGZLyMF8GbH45lSt4iVDqds6ho7Lxq2Yevh9-IU023jIJu6muYrTelpL0nAgWYqjAAEsvv1Be2d09SwU7SZ"
                remoteMsgHeaders!![REMOTE_MSG_CONTENT_TYPE] = "application/json"
            }
            return remoteMsgHeaders as HashMap<String, String>
        }
    }
}