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
    @field:Transient
    var user_name: String = "",
    @DocumentId
    val id: String = "",
) : java.io.Serializable {
    override fun toString(): String {
        return "Review(id=$id, movie_id=$movie_id, user_id=$user_id, detail=$detail, date=$date, rating=$rating, user_name=$user_name)"
    }
}