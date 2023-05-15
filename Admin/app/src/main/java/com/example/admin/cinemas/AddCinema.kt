package com.example.admin.cinemas

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.example.admin.R
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddCinema : AppCompatActivity() {
    var cinemaNameET: EditText? = null
    var cinemaImgURLET: EditText? = null
    var cinemaAddrET: EditText? = null
    var cinemaBigRB: RadioButton? = null
    var cinemaSmallRB: RadioButton? = null
    var cinemaTypeRG: RadioGroup? = null
    var cinemaPriceTV: TextView? = null
    var cinemaPriceET: EditText? = null
    var saveBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cinema)

        cinemaNameET = findViewById(R.id.addCinemaNameET)
        cinemaImgURLET = findViewById(R.id.addCinemaImgET)
        cinemaAddrET = findViewById(R.id.addCinemaAddrET)
        cinemaBigRB = findViewById(R.id.addCinemaBigRB)
        cinemaSmallRB = findViewById(R.id.addCinemaSmallRB)
        cinemaTypeRG = findViewById(R.id.addCinemaTypeRG)
        cinemaPriceTV = findViewById(R.id.addCinemaPriceTV)
        cinemaPriceET = findViewById(R.id.addCinemaPriceET)
        saveBtn = findViewById(R.id.addCinemaSaveBtn)

        cinemaBigRB!!.setOnClickListener {
            cinemaPriceTV!!.visibility = View.VISIBLE
            cinemaPriceET!!.visibility = View.VISIBLE
        }
        cinemaSmallRB!!.setOnClickListener {
            cinemaPriceTV!!.visibility = View.GONE
            cinemaPriceET!!.visibility = View.GONE
        }

        saveBtn!!.setOnClickListener {
            if (cinemaNameET!!.text.toString().isEmpty() ||
                cinemaAddrET!!.text.toString().isEmpty()
            ) {
                Toast.makeText(this, "Vui lòng nhập tên và địa chỉ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var type = ""
            when (cinemaTypeRG!!.checkedRadioButtonId) {
                R.id.addCinemaBigRB -> {
                    type = "Big"
                }
                R.id.addCinemaSmallRB -> {
                    type = "Small"
                }
            }

            addCinema(
                cinemaImgURLET!!.text.toString(),
                cinemaNameET!!.text.toString(),
                cinemaAddrET!!.text.toString(),
                type,
                cinemaPriceET!!.text.toString().toIntOrNull() ?: 0
            )
        }
    }

    fun addCinema(img_url: String, name: String, addr: String, type: String, price: Int) {
        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore
        db.collection("cinema")
            .add(Cinema(img_url, name, addr, type = type, price = price))
            .addOnSuccessListener {
                val replyIntent = Intent()
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error adding document", e)
            }
    }
}