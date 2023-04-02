package com.example.admin.cinemas

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class Cinema(
    val img_url: String = "",
    val name: String = "",
    val address: String = "",
    val auditoriums_no: Int = 0,
    val status: String = "Open",
    @field:JvmField
    val is_deleted: Boolean = false
) {

}