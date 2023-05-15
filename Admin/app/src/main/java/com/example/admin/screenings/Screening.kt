package com.example.admin.screenings

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Screening (
    val auditorium_id: String = "",
    val cinema_id: String = "",
    val movie_id: String = "",
    val screening_start: Date = Date(),
    val screening_end: Date = Date(),
    @field:JvmField
    val is_deleted: Boolean = false,
    @DocumentId
    val id: String = "",
) : java.io.Serializable {

}