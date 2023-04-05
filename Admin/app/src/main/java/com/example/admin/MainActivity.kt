package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.admin.cinemas.CinemaList
import com.example.admin.cinemas.EditCinema
import com.example.admin.seats.SeatScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.admin.movies.MovieList

class MainActivity : AppCompatActivity() {
    var goToCinemaBtn: Button? = null
    var goToMovieBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToCinemaBtn = findViewById(R.id.goToCinemaBtn)
        goToMovieBtn = findViewById(R.id.goToMovieBtn)

        goToCinemaBtn!!.setOnClickListener {
            val intent = Intent(this, CinemaList::class.java)
            startActivity(intent)
        }
        goToMovieBtn!!.setOnClickListener {
            val intent = Intent(this, MovieList::class.java)
            startActivity(intent)
        }

    }
}