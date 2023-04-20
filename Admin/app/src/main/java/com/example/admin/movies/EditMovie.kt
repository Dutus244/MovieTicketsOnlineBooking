package com.example.admin.movies

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.admin.R
import com.example.admin.cinemas.Cinema
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class EditMovie : AppCompatActivity() {
    var movieNameET: EditText? = null
    var movieCastET: EditText? = null
    var movieDirectorET: EditText? = null
    var moviePosterURLET: EditText? = null
    var movieVideoURLET: EditText? = null
    var movieClassificationRG: RadioGroup? = null
    var movieRealeasedDateET: EditText? = null
    var movieDescriptionET: EditText? = null
    var movieActiveRG: RadioGroup? = null
    var movieRatingET: TextView? = null
    var movieDurationET: EditText? = null
    var delBtn: Button? = null
    var saveBtn: Button? = null

    var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_movie)
        FirebaseApp.initializeApp(this@EditMovie)

        movieNameET = findViewById(R.id.editMovieNameET)
        movieCastET = findViewById(R.id.editMovieCastET)
        movieDirectorET = findViewById(R.id.editMovieDirectorET)
        moviePosterURLET = findViewById(R.id.editMoviePosterET)
        movieVideoURLET = findViewById(R.id.editMovieVideoET)
        movieClassificationRG = findViewById(R.id.editMovieClassificationRG)
        movieRealeasedDateET = findViewById(R.id.editMovieReleasedDateET)
        movieDescriptionET = findViewById(R.id.editMovieDescriptionET)
        movieActiveRG = findViewById(R.id.editMovieActiveRG)
        movieRatingET = findViewById(R.id.editMovieRatingET)
        movieDurationET = findViewById(R.id.editMovieDurationET)
        delBtn = findViewById(R.id.editMovieDeleteBtn)
        saveBtn = findViewById(R.id.editMovieSaveBtn)

        val dateTextView = findViewById<ImageView>(R.id.calendar_icon)
        var cal = Calendar.getInstance()
        movieRealeasedDateET!!.setText(
            "${Calendar.DAY_OF_MONTH}/${Calendar.MONTH+1}/${Calendar.YEAR}")
        dateTextView.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this@EditMovie,
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

        val intent = intent
        movie = intent.getSerializableExtra("movie") as? Movie

        movieNameET!!.setText(movie?.title)
        movieCastET!!.setText(movie?.cast)
        movieDirectorET!!.setText(movie?.director)
        moviePosterURLET!!.setText(movie?.poster_url)
        movieVideoURLET!!.setText(movie?.vid_url)
        val radioButtonId = when (movie?.classification) {
            "P" -> R.id.radioButton1
            "C13" -> R.id.radioButton2
            "C16" -> R.id.radioButton3
            "C18" -> R.id.radioButton4
            else -> -1
        }
        if (radioButtonId != -1) movieClassificationRG?.check(radioButtonId)
        movieRealeasedDateET!!.setText(SimpleDateFormat("dd/MM/yyyy",
        Locale.getDefault()).format(movie?.release_date!!))
        movieDescriptionET!!.setText(movie?.description)
        val radioButtonId2 = when (movie?.is_active) {
            true -> R.id.radioButton5
            false -> R.id.radioButton6
            else -> -1
        }
        if (radioButtonId2 != -1) movieActiveRG!!.check(radioButtonId2)
        movieRatingET!!.setText(movie?.rating.toString())
        movieDurationET!!.setText(movie?.duration.toString())

        delBtn!!.setOnClickListener {
            val dialog = createDeleteDialog()
            dialog.show()
        }

        saveBtn!!.setOnClickListener {
            editMovie()
        }
    }
    fun editMovie() {
        val db = Firebase.firestore
        db.collection("movie")
            .document(movie!!.id).set(
                Movie(movieNameET!!.text.toString(),
                    movieCastET!!.text.toString(),
                    movieDirectorET!!.text.toString(),
                    moviePosterURLET!!.text.toString(),
                    movieVideoURLET!!.text.toString(),
                    findViewById<RadioButton>(movieClassificationRG!!.checkedRadioButtonId).text.toString(),
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(movieRealeasedDateET!!.text.toString()),
                    movieDurationET!!.text.toString().toInt(),
                    movieDescriptionET!!.text.toString(),
                    movieRatingET!!.text.toString().toDouble(),
                    movieActiveRG!!.checkedRadioButtonId == R.id.radioButton5
                )
            )
            .addOnSuccessListener {
                val replyIntent = Intent()
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error adding document", e)
            }
    }
    fun delMovie(movie: Movie){
        val db = Firebase.firestore
        db.collection("movie")
            .document(movie!!.id)
            .update("is_deleted", true)
            .addOnSuccessListener {
                val replyIntent = Intent()
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
            .addOnFailureListener { exception ->
                Log.w("DB", "Error getting documents.", exception)
            }
    }
    fun createDeleteDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this@EditMovie)
        builder.setMessage("Bạn có chắc là muốn xóa!")
            .setPositiveButton("Có") { dialog, id ->
                val db = Firebase.firestore
                db.collection("movie")
                    .document(movie!!.id)
                    .update("is_deleted", true)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this@EditMovie,
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
            .setNegativeButton("Không") { dialog, id ->

            }
        return builder.create()
    }
}