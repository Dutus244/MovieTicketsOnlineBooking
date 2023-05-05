package com.example.movieticketsonlinebooking.viewmodels

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*
import kotlin.collections.ArrayList

@IgnoreExtraProperties
class Reservation(
    var auditorium_id: String = "",
    var screening_id: String = "",
    var user_id: String = "",
    var total_price: Int = 0,
    var date: Date = Date(),
    var seats: ArrayList<Int> = arrayListOf(),
    @DocumentId
    val id: String = "",
) : java.io.Serializable {
}