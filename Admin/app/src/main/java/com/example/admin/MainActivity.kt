package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.admin.cinemas.CinemaList
import com.example.admin.movies.MovieList
import com.example.admin.reviews.MovieReviewList
import com.example.admin.screenings.CinemaScreeningList

class MainActivity : AppCompatActivity() {
    var goToCinemaBtn: Button? = null
    var goToMovieBtn: Button? = null
    var goToScreeningBtn: Button? = null
    var goToReviewBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToCinemaBtn = findViewById(R.id.goToCinemaBtn)
        goToMovieBtn = findViewById(R.id.goToMovieBtn)
        goToScreeningBtn = findViewById(R.id.goToReviewBtn)
        goToReviewBtn = findViewById(R.id.goToReviewBtn)

        goToCinemaBtn!!.setOnClickListener {
            val intent = Intent(this, CinemaList::class.java)
            startActivity(intent)
        }
        goToMovieBtn!!.setOnClickListener {
            val intent = Intent(this, MovieList::class.java)
            startActivity(intent)
        }
        goToScreeningBtn!!.setOnClickListener {
            val intent = Intent(this, CinemaScreeningList::class.java)
            startActivity(intent)
        }
        goToReviewBtn!!.setOnClickListener {
            val intent = Intent(this, MovieReviewList::class.java)
            startActivity(intent)
        }
    }
}