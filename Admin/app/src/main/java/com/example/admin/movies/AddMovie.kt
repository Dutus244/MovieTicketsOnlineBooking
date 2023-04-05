package com.example.admin.movies

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.R
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddMovie : AppCompatActivity() {
    var movieNameET: EditText? = null
    var movieCastET: EditText? = null
    var movieDirectorET: EditText? = null
    var moviePosterURLET: EditText? = null
    var movieVideoURLET: EditText? = null
    var movieClassificationRG: RadioGroup? = null
    var movieRealeasedDateET: EditText? = null
    var movieDescriptionET: EditText? = null
    var saveBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)
        FirebaseApp.initializeApp(this@AddMovie)

        movieNameET = findViewById(R.id.addMovieNameET)
        movieCastET = findViewById(R.id.addMovieCastET)
        movieDirectorET = findViewById(R.id.addMovieDirectorET)
        moviePosterURLET = findViewById(R.id.addMoviePosterET)
        movieVideoURLET = findViewById(R.id.addMovieVideoET)
        movieClassificationRG = findViewById(R.id.addMovieClassificationRG)
        movieRealeasedDateET = findViewById(R.id.addMovieReleasedDateET)
        movieDescriptionET = findViewById(R.id.addMovieDescriptionET)
        saveBtn = findViewById(R.id.addMovieSaveBtn)

        val dateTextView = findViewById<ImageView>(R.id.calendar_icon)
        var cal = Calendar.getInstance()
        movieRealeasedDateET!!.setText(
            "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH)+1}/${cal.get(Calendar.YEAR)}")
        dateTextView.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this@AddMovie,
                { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "dd/MM/yyyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    movieRealeasedDateET!!.setText(sdf.format(cal.time))
            }, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
        }

        saveBtn!!.setOnClickListener {
            if (movieNameET!!.text.toString().isEmpty()
            ) {
                Toast.makeText(this, "No", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addMovie(
                movieNameET!!.text.toString(),
                movieCastET!!.text.toString(),
                movieDirectorET!!.text.toString(),
                moviePosterURLET!!.text.toString(),
                movieVideoURLET!!.text.toString(),
                findViewById<RadioButton>(movieClassificationRG!!.checkedRadioButtonId).text.toString(),
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(movieRealeasedDateET!!.text.toString()),
                movieDescriptionET!!.text.toString(),
            )
        }
    }
    fun addMovie(title: String, cast: String, director: String,
                 poster_url: String, vid_url: String, classification: String,
                 release_date: Date, description: String) {
        val db = Firebase.firestore
        db.collection("movie")
            .add(Movie(title, cast, director, poster_url, vid_url,
                classification, release_date, description))
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