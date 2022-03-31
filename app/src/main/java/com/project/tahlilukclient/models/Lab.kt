package com.project.tahlilukclient.models

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

class Lab : Serializable {
    var name: String? = null
    var image: String? = null
    var address: String? = null
    var phone: String? = null
    var labLatitude : String?=null
    var Analytics: ArrayList<Analytics>? = null
    var labLongitude:String?= name
    var token: String? = null
    var id: String? = null
}