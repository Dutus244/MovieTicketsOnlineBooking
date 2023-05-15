package com.example.admin.cinemas

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.admin.R
import com.example.admin.auditoriums.AuditoriumList
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditCinema : AppCompatActivity() {
    var cinemaNameET: EditText? = null
    var cinemaImgURLET: EditText? = null
    var cinemaAddrET: EditText? = null
    var cinemaAudiNumTV: TextView? = null
    var statusRadioGroup: RadioGroup? = null
    var cinemaTypeTV: TextView? = null
    var cinemaPriceTV: TextView? = null
    var cinemaPriceET: EditText? = null
    var audiBtn: Button? = null
    var delBtn: Button? = null
    var saveBtn: Button? = null

    var cinema: Cinema? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_cinema)
        FirebaseApp.initializeApp(this@EditCinema)


        val intent = intent
        cinema = intent.getSerializableExtra("cinema") as? Cinema

        cinemaNameET = findViewById(R.id.editCinemaNameET)
        cinemaImgURLET = findViewById(R.id.editCinemaImgET)
        cinemaAddrET = findViewById(R.id.editCinemaAddrET)
        cinemaAudiNumTV = findViewById(R.id.editCinemaAudiNumTV2)
        statusRadioGroup = findViewById(R.id.editCinemaStatusRG)
        cinemaTypeTV = findViewById(R.id.editCinemaTypeTV)
        cinemaPriceTV = findViewById(R.id.editCinemaPriceTV)
        cinemaPriceET = findViewById(R.id.editCinemaPriceET)
        audiBtn = findViewById(R.id.editCinemaAudiBtn)
        delBtn = findViewById(R.id.editCinemaDeleteBtn)
        saveBtn = findViewById(R.id.editCinemaSaveBtn)

        cinemaNameET!!.setText(cinema?.name)
        cinemaImgURLET!!.setText(cinema?.img_url)
        cinemaAddrET!!.setText(cinema?.address)
        cinemaAudiNumTV!!.text = cinema?.auditoriums_no.toString()
        when (cinema?.status) {
            "Open" -> {
                findViewById<RadioButton>(R.id.editCinemaStatusRB1).isChecked = true
            }
            "Closed" -> {
                findViewById<RadioButton>(R.id.editCinemaStatusRB2).isChecked = true
            }
        }
        when (cinema?.type) {
            "Big" -> {
                cinemaTypeTV!!.append("thường")
                cinemaPriceET!!.setText(cinema?.price.toString())
            }
            "Small" -> {
                cinemaTypeTV!!.append("mini")
                cinemaPriceTV!!.visibility = View.GONE
                cinemaPriceET!!.visibility = View.GONE
            }
        }

        audiBtn!!.setOnClickListener {
            val intent = Intent(this, AuditoriumList::class.java)
            intent.putExtra("cinema_id", cinema!!.id)
            intent.putExtra("cinema_type", cinema!!.type)
            startActivity(intent)
        }
        delBtn!!.setOnClickListener {
            val dialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage("Bạn có chắc là muốn xóa!")
                    .setPositiveButton("Có") { dialog, id ->
                        val db = Firebase.firestore
                        db.collection("cinema")
                            .document(cinema!!.id)
                            .update("is_deleted", true)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Xóa thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val replyIntent = Intent()
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Log.w("DB", "Error getting documents.", exception)
                            }
                    }
                    .setNegativeButton("Không", DialogInterface.OnClickListener { dialog, id ->

                    })
                builder.create()
            }
            if (dialog != null) {
                dialog!!.show()
            }
        }
        saveBtn!!.setOnClickListener {
            var updates = hashMapOf<String, Any>()
            when (cinema?.type) {
                "Big" -> {
                    if (cinemaNameET!!.text.toString().isEmpty() ||
                        cinemaAddrET!!.text.toString().isEmpty() ||
                        cinemaPriceET!!.text.toString().isEmpty()
                    ) {
                        Toast.makeText(this, "Vui lòng nhập tên và địa chỉ và giá", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                    updates = hashMapOf<String, Any>(
                        "img_url" to cinemaImgURLET!!.text.toString(),
                        "name" to cinemaNameET!!.text.toString(),
                        "address" to cinemaAddrET!!.text.toString(),
                        "status" to findViewById<RadioButton>(
                            statusRadioGroup!!.checkedRadioButtonId).text.toString(),
                        "price" to cinemaPriceET!!.text.toString().toInt()
                    )
                }
                "Small" -> {
                    if (cinemaNameET!!.text.toString().isEmpty() ||
                        cinemaAddrET!!.text.toString().isEmpty()
                    ) {
                        Toast.makeText(this, "Vui lòng nhập tên và địa chỉ và giá", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                    updates = hashMapOf<String, Any>(
                        "img_url" to cinemaImgURLET!!.text.toString(),
                        "name" to cinemaNameET!!.text.toString(),
                        "address" to cinemaAddrET!!.text.toString(),
                        "status" to findViewById<RadioButton>(
                            statusRadioGroup!!.checkedRadioButtonId).text.toString(),
                    )
                }
            }

            val db = Firebase.firestore
            db.collection("cinema")
                .document(cinema!!.id)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show()
                    val replyIntent = Intent()
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }
                .addOnFailureListener { exception ->
                    Log.w("DB", "Error getting documents.", exception)
                }
        }

    }
}