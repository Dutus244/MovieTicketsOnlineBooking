package com.example.admin.movies

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Movie(
    val title: String = "",
    val poster_url: String = "",
    val vid_url: String = "",
    val director: String = "",
    val cast: String = "",
    val description: String = "",
    val release_date: Date = Date(),
    val classification: String = "",
    val rating: Double = 0.0,
    @field:JvmField
    val is_active: Boolean = true,
    @field:JvmField
    val is_deleted: Boolean = false,
    @DocumentId
    val id: String = "",
) : java.io.Serializable {

}