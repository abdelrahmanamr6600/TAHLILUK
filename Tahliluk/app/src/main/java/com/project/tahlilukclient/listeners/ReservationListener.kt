package com.project.tahlilukclient.listeners

import com.project.tahlilukclient.models.Reserve

interface ReservationListener {
    fun onReservationClickListener(reserve:Reserve)

}