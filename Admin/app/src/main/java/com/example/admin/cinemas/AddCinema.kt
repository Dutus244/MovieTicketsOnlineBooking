package com.example.admin.cinemas

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
    var saveBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cinema)

        cinemaNameET = findViewById(R.id.addCinemaNameET)
        cinemaImgURLET = findViewById(R.id.addCinemaImgET)
        cinemaAddrET = findViewById(R.id.addCinemaAddrET)
        saveBtn = findViewById(R.id.addCinemaSaveBtn)

        saveBtn!!.setOnClickListener {
            if (cinemaNameET!!.text.toString().isEmpty() ||
                cinemaAddrET!!.text.toString().isEmpty()) {
                Toast.makeText(this,"Vui lòng nhập tên và địa chỉ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addCinema(
                cinemaImgURLET!!.text.toString(),
                cinemaNameET!!.text.toString(),
                cinemaAddrET!!.text.toString()
            )
            val replyIntent = Intent()
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
    }

    fun addCinema(img_url: String, name: String, addr: String) {
        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore
        db.collection("cinema")
            .add(Cinema(img_url, name, addr))
            .addOnSuccessListener { documentReference ->
//                Log.d("DB", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error adding document", e)
            }
    }
}