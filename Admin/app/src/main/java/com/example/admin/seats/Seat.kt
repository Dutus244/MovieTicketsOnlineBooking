package com.example.admin.seats

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Seat(
    val row: String = "",
    val col: String = "",
    val auditorium_id: String = "",
    @DocumentId
    val id: String = "",
) {
}