package com.example.admin.reservations

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.Date

@IgnoreExtraProperties
class Reservation(
    var screening_id: String = "",
    var user_id: String = "",
    var total_price: Int = 0,
    var date: Date = Date(),
    @DocumentId
    val id: String = "",
) : java.io.Serializable {
}