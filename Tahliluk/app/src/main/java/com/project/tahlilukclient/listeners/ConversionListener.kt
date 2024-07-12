package com.project.tahlilukclient.listeners

import com.project.tahlilukclient.models.Lab

interface ConversionListener {
    fun onConversionClicked(lab: Lab)
}