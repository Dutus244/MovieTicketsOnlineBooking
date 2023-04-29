package com.example.admin.reservations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.admin.R

class ReservationForBigDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_for_big_detail)

        val intent = intent
        val reservation = intent.getSerializableExtra("reservation") as? CustomReservation
        Log.i("Test", reservation!!.seats.toString())
    }
}