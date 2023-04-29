package com.example.admin.reservations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.admin.R
import com.google.firebase.FirebaseApp

class ReservationForBigDetail : AppCompatActivity() {
    private var userTV: TextView? = null
    private var dateTV: TextView? = null
    private var curSeatTV: TextView? = null
    private var curSeatPriceTV: TextView? = null
    private var allSeatsTV: TextView? = null
    private var totalPriceTV: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_for_big_detail)
        FirebaseApp.initializeApp(this@ReservationForBigDetail)

        val intent = intent
        val user = intent.getStringExtra("user")
        val curSeat = intent.getStringExtra("curSeat")
        val reservation = intent.getSerializableExtra("reservation") as? Reservation
        val titles = intent.getStringArrayListExtra("titles")
        val allSeats = titles!!.filterIndexed { index, s ->
            reservation!!.seats.contains(index)
        }

        userTV = findViewById(R.id.userTV)
        dateTV = findViewById(R.id.dateTV)
        curSeatTV = findViewById(R.id.curSeatTV)
        curSeatPriceTV = findViewById(R.id.curSeatPriceTV)
        allSeatsTV = findViewById(R.id.allSeatsTV)
        totalPriceTV = findViewById(R.id.totalPriceTV)

        userTV!!.append(user)
        dateTV!!.append(reservation!!.date.toString())
        curSeatTV!!.append(curSeat)
        curSeatPriceTV!!.append((reservation.total_price / allSeats.size).toString())
        allSeatsTV!!.append(allSeats.joinToString(", "))
        totalPriceTV!!.append(reservation.total_price.toString())
    }
}