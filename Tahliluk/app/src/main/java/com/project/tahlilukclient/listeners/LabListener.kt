package com.project.tahlilukclient.listeners

import com.project.tahlilukclient.models.Lab

interface LabListener {
    fun onLabClicked(lab: Lab)
}