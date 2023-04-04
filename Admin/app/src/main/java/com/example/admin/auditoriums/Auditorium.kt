package com.example.admin.auditoriums

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Auditorium(
    var name: String = "",
    var seats_no: Int = 0,
    @field:JvmField
    var is_deleted: Boolean = false,
    var cinema_id: String = "",
    var map: ArrayList<String> = arrayListOf(),
    @DocumentId
    val id: String = "",
) : java.io.Serializable {
}