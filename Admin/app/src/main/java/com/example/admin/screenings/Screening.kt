package com.example.admin.screenings

import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.*
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Screening (
    var movie_name: String = "",
    var auditorium_name: String = "",
    val auditorium_id: String = "",
    val cinema_id: String = "",
    val movie_id: String = "",
    val screening_start: Timestamp = now(),
    val screening_end: Timestamp = now(),
    @field:JvmField
    val is_deleted: Boolean = false,
    @DocumentId
    val id: String = "",
) : java.io.Serializable {

}