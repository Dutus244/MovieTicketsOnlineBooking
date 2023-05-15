package com.example.admin.seats

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.admin.R
import com.example.admin.auditoriums.Auditorium
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class SeatScreenTypeSmall : AppCompatActivity() {
    var img: ImageView? = null
    var priceET: EditText? = null
    var imgURL: EditText? = null
    var description: EditText? = null
    var deleteBtn: Button? = null
    var saveBtn: Button? = null


    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_screen_type_small)
        FirebaseApp.initializeApp(this@SeatScreenTypeSmall)

        intent = intent
        val auditorium = intent.getSerializableExtra("auditorium") as Auditorium

        img = findViewById(R.id.img)
        priceET = findViewById(R.id.priceET)
        imgURL = findViewById(R.id.imgURLET)
        description = findViewById(R.id.descriptionET)
        deleteBtn = findViewById(R.id.editAudiDeleteBtn)
        saveBtn = findViewById(R.id.editAudiSaveBtn)

        if (!auditorium.img_url.isEmpty()) {
            Picasso.get().load(auditorium.img_url)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(img);
        }
        priceET!!.setText(auditorium.price.toString())
        imgURL!!.setText(auditorium.img_url)
        description!!.setText(auditorium.description)

        deleteBtn!!.setOnClickListener {
            val dialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage("Bạn có chắc là muốn xóa!")
                    .setPositiveButton("Có", DialogInterface.OnClickListener { dialog, id ->
                        db.collection("auditorium")
                            .document(auditorium.id)
                            .update("is_deleted", true)
                            .addOnSuccessListener {
                                db.collection("cinema")
                                    .document(auditorium.cinema_id)
                                    .update("auditoriums_no", FieldValue.increment(-1))
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
                                    .addOnFailureListener { e ->
                                        Log.w("DB", "Error getting documents.", e)
                                    }
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
            val updates = hashMapOf<String, Any>(
                "price" to priceET!!.text.toString().toInt(),
                "img_url" to imgURL!!.text.toString(),
                "description" to description!!.text.toString(),
            )
            db.collection("auditorium")
                .document(auditorium.id)
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