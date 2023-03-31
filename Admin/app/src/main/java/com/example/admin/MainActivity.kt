package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.admin.cinemas.AddCinema

class MainActivity : AppCompatActivity() {
    var goToCinemaBtn: Button? = null
    var goToMovieBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToCinemaBtn = findViewById(R.id.goToCinemaBtn)
        goToMovieBtn = findViewById(R.id.goToMovieBtn)

        goToCinemaBtn!!.setOnClickListener {
            val intent = Intent(this, AddCinema::class.java)
            startActivity(intent)
        }
        goToMovieBtn!!.setOnClickListener {

        }
    }
}