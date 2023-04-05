package com.example.admin.screenings

import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.*
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Screening (
    val auditorium_id: String = "",
    val cinema_id: String = "",
    val movie_id: String = "",
    val screening_start: Timestamp = now(),
    val screening_end: Timestamp = now(),
    @field:JvmField
    val is_deleted: Boolean = false,
    @field:Exclude
    var movie_name: String = "",
    @field:Exclude
    var auditorium_name: String = "",
    @DocumentId
    val id: String = "",
) : java.io.Serializable {

}
@IgnoreExtraProperties
data class ScreeningWithoutNames (
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
