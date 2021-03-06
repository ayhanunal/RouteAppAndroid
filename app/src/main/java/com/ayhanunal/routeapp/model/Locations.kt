package com.ayhanunal.routeapp.model

import java.util.*

data class Locations(
    var uuid: String,
    var name: String,
    var description: String,
    var latitude: String,
    var longitude: String,
    var isActive: Boolean,
    var priority: Int,
    var savedPhone: String,
    var timeStamp: Int,
    var distance: Float,
    var estimatedTime: Int,
    var documentID: String,
    var address: String,
    var price: String,
)