package com.example.admin.reservations

import com.example.admin.seats.Seat
import com.example.admin.tickets.Ticket
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*
import kotlin.collections.ArrayList

@IgnoreExtraProperties
class CustomReservation(
    var screening_id: String = "",
    var user_id: String = "",
    var total_price: Int = 0,
    var date: Date = Date(),
    var seats: ArrayList<String> = arrayListOf(),
    @DocumentId
    val id: String = "",
) : java.io.Serializable {
}