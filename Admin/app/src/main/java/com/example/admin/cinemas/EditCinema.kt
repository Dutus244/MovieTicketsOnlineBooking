package com.example.admin.cinemas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.example.admin.R
import com.example.admin.auditoriums.AuditoriumList

class EditCinema : AppCompatActivity() {
    var cinemaNameTV: TextView? = null
    var cinemaImgURLTV: TextView? = null
    var cinemaAddrTV: TextView? = null
    var cinemaAudiNumTV: TextView? = null
    var statusRadioGroup: RadioGroup? = null
    var statusRadioButton: RadioButton? = null
    var audiBtn: Button? = null
    var delBtn: Button? = null
    var saveBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_cinema)

        cinemaNameTV = findViewById(R.id.editCinemaNameTV)
        cinemaImgURLTV = findViewById(R.id.editCinemaImgTV)
        cinemaAddrTV = findViewById(R.id.editCinemaAddrTV)
        cinemaAudiNumTV = findViewById(R.id.editCinemaAudiNumTV)
        statusRadioGroup = findViewById(R.id.editCinemaStatusRG)
        audiBtn = findViewById(R.id.editCinemaAudiBtn)
        delBtn = findViewById(R.id.editCinemaDeleteBtn)
        saveBtn = findViewById(R.id.editCinemaSaveBtn)

        audiBtn!!.setOnClickListener {
            val intent = Intent(this, AuditoriumList::class.java)
            startActivity(intent)
        }
        delBtn!!.setOnClickListener {
            finish()
        }
        saveBtn!!.setOnClickListener {
            finish()
        }
    }
}