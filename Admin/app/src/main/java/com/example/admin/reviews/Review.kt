package com.example.admin.reviews

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Review(
    val movie_id: String = "",
    val user_id: String = "",
    val detail: String = "",
    val date: Date = Date(),
    val rating: Double = 0.0,
    var user_name: String = "",
    @DocumentId
    val id: String = "",
) : java.io.Serializable {

}