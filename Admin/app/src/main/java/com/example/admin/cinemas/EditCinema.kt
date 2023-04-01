package com.example.admin.cinemas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.example.admin.R

class EditCinema : AppCompatActivity() {
    var cinemaNameTV: TextView? = null
    var cinemaImgURLTV: TextView? = null
    var cinemaAddrTV: TextView? = null
    var cinemaAudiNumTV: TextView? = null
    var statusRadioGroup: RadioGroup? = null
    var statusRadioButton: RadioButton? = null
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
        delBtn = findViewById(R.id.editCinemaDeleteBtn)
        saveBtn = findViewById(R.id.editCinemaSaveBtn)

        delBtn!!.setOnClickListener {
            finish()
        }
        saveBtn!!.setOnClickListener {
            finish()
        }
    }
}