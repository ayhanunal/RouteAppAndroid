package com.ayhanunal.routeapp.model

data class Memories(
    var memName: String,
    var memDescription: String,
    var memLatitude: String,
    var memLongitude: String,
    var memIsActive: Boolean,
    var memPriority: Int,
    var memSavedPhone: String,
    var memTimeStamp: Int,
    var memDate: String,
    var memDocumentID: String,
    var memImageUrl: String
)
