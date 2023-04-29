package com.example.admin.tickets

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Ticket(
    var reservation_id: String = "",
    var screening_id: String = "",
    var seat_id: String = "",
    var price: Int = 0,
    @DocumentId
    val id: String = "",
) : java.io.Serializable {
}