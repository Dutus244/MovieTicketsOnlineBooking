package com.example.admin.reservations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.admin.R
import com.google.firebase.FirebaseApp
import java.text.SimpleDateFormat
import java.util.*

class ReservationForBigDetail : AppCompatActivity() {
    private var userTV: TextView? = null
    private var dateTV: TextView? = null
    private var movieNameTV: TextView? = null
    private var audiNameTV: TextView? = null
    private var allSeatsTV: TextView? = null
    private var totalPriceTV: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_for_big_detail)
        FirebaseApp.initializeApp(this@ReservationForBigDetail)

        val intent = intent
        val user = intent.getStringExtra("user")
        val reservation = intent.getSerializableExtra("reservation") as? Reservation
        val movie_title = intent.getStringExtra("movie_title")!!
        val auditorium_name = intent.getStringExtra("auditorium_name")
        val titles = intent.getStringArrayListExtra("titles")
        val allSeats = titles!!.filterIndexed { index, s ->
            reservation!!.seats.contains(index)
        }

        userTV = findViewById(R.id.userTV)
        dateTV = findViewById(R.id.dateTV)
        movieNameTV = findViewById(R.id.reservationBigMovieNameTV)
        audiNameTV = findViewById(R.id.reservationBigAudiNameTV)
        allSeatsTV = findViewById(R.id.allSeatsTV)
        totalPriceTV = findViewById(R.id.totalPriceTV)

        userTV!!.append(user)
        dateTV!!.append(
            SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss",
                Locale.getDefault()
            ).format(reservation!!.date)
        )
        movieNameTV!!.append(movie_title)
        audiNameTV!!.append(auditorium_name)
        allSeatsTV!!.append(allSeats.joinToString(", "))
        totalPriceTV!!.append(reservation.total_price.toString())
    }
}