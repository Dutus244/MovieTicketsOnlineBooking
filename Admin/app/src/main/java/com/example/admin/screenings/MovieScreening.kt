package com.example.admin.screenings

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class MovieScreening (
    val title: String = "",
    val cast: String = "",
    val director: String = "",
    val poster_url: String = "",
    val vid_url: String = "",
    val classification: String = "",
    val release_date: Date = Date(),
    val duration: Int = 0,
    val description: String = "",
    val rating: Double = 0.0,
    @field:JvmField
    val is_active: Boolean = true,
    @field:JvmField
    val is_deleted: Boolean = false,
    @field:Transient
    var screenings: List<Screening> = emptyList(),
    @DocumentId
    val id: String = "",
) : java.io.Serializable {

}
