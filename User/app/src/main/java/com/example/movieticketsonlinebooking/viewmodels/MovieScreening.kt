package com.example.movieticketsonlinebooking.viewmodels

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class MovieScreening(
    val title: String = "",
    val cast: String = "",
    val director: String = "",
    val poster_url: String = "",
    val vid_url: String = "",
    val classification: String = "",
    val release_date: Date = Date(),
    val description: String = "",
    val rating: Double = 0.0,
    val duration: Int = 0,
    @field:JvmField
    val is_active: Boolean = true,
    @field:JvmField
    val is_deleted: Boolean = false,
    var screenings: List<Screening>? = null,
    @DocumentId
    val id: String = "",
) : java.io.Serializable {

}