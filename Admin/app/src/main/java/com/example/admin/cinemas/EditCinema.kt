package com.example.admin.cinemas

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.fragment.app.DialogFragment
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
    var statusRadioButton: RadioButton? = null
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

        audiBtn!!.setOnClickListener {
            val intent = Intent(this, AuditoriumList::class.java)
            startActivity(intent)
        }
        delBtn!!.setOnClickListener {
            val dialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage("Bạn có chắc là muốn xóa!")
                    .setPositiveButton("Có", DialogInterface.OnClickListener { dialog, id ->
                        val db = Firebase.firestore
                        db.collection("cinema")
                            .document(cinema!!.id)
                            .update("is_deleted", true)
                            .addOnSuccessListener {
                                val replyIntent = Intent()
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Log.w("DB", "Error getting documents.", exception)
                            }

                    })
                    .setNegativeButton("Không", DialogInterface.OnClickListener { dialog, id ->

                    })
                builder.create()
            }
            if (dialog != null) {
                dialog!!.show()
            }
        }
        saveBtn!!.setOnClickListener {
            if (cinemaNameET!!.text.toString().isEmpty() ||
                cinemaAddrET!!.text.toString().isEmpty()
            ) {
                Toast.makeText(this, "Vui lòng nhập tên và địa chỉ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val db = Firebase.firestore
            db.collection("cinema")
                .document(cinema!!.id)
                .set(
                    Cinema(
                        cinemaImgURLET!!.text.toString(),
                        cinemaNameET!!.text.toString(),
                        cinemaAddrET!!.text.toString(),
                        cinemaAudiNumTV!!.text.toString().toInt(),
                        findViewById<RadioButton>(statusRadioGroup!!.checkedRadioButtonId).text.toString(),
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Log.w("DB", "Error getting documents.", exception)
                }
            finish()
        }
    }
}