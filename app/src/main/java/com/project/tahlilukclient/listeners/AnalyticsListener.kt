package com.project.tahlilukclient.listeners

import com.project.tahlilukclient.models.Analytics
import com.project.tahlilukclient.models.Lab

interface AnalyticsListener {
    fun onAnalysisClicked(analyticsList:ArrayList<Analytics>)
}