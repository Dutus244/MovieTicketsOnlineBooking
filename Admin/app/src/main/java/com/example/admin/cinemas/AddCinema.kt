package com.example.admin.cinemas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.admin.R

class AddCinema : AppCompatActivity() {
    var cinemaNameTV: TextView? = null
    var cinemaImgURLTV: TextView? = null
    var cinemaAddrTV: TextView? = null
    var cinemaAudiNumTV: TextView? = null
    var saveBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cinema)

        cinemaNameTV = findViewById(R.id.addCinemaNameTV)
        cinemaImgURLTV = findViewById(R.id.addCinemaImgTV)
        cinemaAddrTV = findViewById(R.id.addCinemaAddrTV)
        cinemaAudiNumTV = findViewById(R.id.addCinemaAudiNumTV)
        saveBtn = findViewById(R.id.addCinemaSaveBtn)

        saveBtn!!.setOnClickListener {
            finish()
        }
    }
}