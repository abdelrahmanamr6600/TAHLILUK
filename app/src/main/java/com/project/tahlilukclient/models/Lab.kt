package com.project.tahlilukclient.models

import java.io.Serializable

class Lab : Serializable {
    var labName: String? = null
    var image: String? = null
    var address: String? = null
    var phoneNumber: String? = null
    var latitude: String? = null
    var analytics: ArrayList<Analytics>? = null
    var longitude: String? = null
    var id: String? = null
    var password: String? = null
    var labVerifiedState: String? = null
    var fcmToken: String? = null
}