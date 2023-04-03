package com.example.admin.auditoriums

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Auditorium(
    val name: String = "",
    val seats_no: Int = 0,
    @field:JvmField
    val is_deleted: Boolean = false,
    val cinema_id: String = "",
    @DocumentId
    val id: String = "",
) {
}