package com.project.tahlilukclient.models

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

class Reserve : Serializable {
    var orderId:String?=null
    var patientId:String? = null
    var labId:String?=null
    var orderLocation:GeoPoint?=null;
    var orderAddress:String?=null
    var analyticsList:ArrayList<Analytics>?=null
    var results :String?=null
    var orderState:String?=null
    var orderUserPhone:String?=null
    var orderDateTime:String?=null
    var orderAnalyticsPrice:String? = null
    var orderTotalAmount:String? = null
    var orderAdditionalInformation:String?=null


}