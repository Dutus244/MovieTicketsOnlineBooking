package com.example.admin.screenings

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.admin.R
import com.example.admin.auditoriums.Auditorium
import com.example.admin.cinemas.Cinema
import com.example.admin.movies.Movie
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class AddScreening : AppCompatActivity() {
    var cinemaNameET: EditText? = null
    var auditoriumNameSpinner: Spinner? = null
    var movieNameSpinner: Spinner? = null
    var startTimeET: EditText? = null
    var saveBtn: Button? = null

    var cinema: Cinema? = null

    var auditoriums: List<Auditorium> = listOf()
    var movies: List<Movie> = listOf()

    var auditoriumChoice: Auditorium? = null
    var movieChoice: Movie? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_screening)
        FirebaseApp.initializeApp(this@AddScreening)

        val intent = intent
        cinema = intent.getSerializableExtra("cinema") as? Cinema

        cinemaNameET = findViewById(R.id.addScreeningCinemaNameET)
        auditoriumNameSpinner = findViewById(R.id.addScreeningAuditoriumSpinner)
        movieNameSpinner = findViewById(R.id.addScreeningMovieSpinner)
        startTimeET = findViewById(R.id.addScreeningStartET)
        saveBtn = findViewById(R.id.addScreeningSaveBtn)

        cinemaNameET!!.setText(cinema!!.name)

        val dateTextView1 = findViewById<ImageView>(R.id.calendar_icon1)
        val cal1 = Calendar.getInstance()
        startTimeET!!.setText(
            "${cal1.get(Calendar.DAY_OF_MONTH)}/${cal1.get(Calendar.MONTH)+1}/${cal1.get(Calendar.YEAR)} " +
                    "${cal1.get(Calendar.HOUR_OF_DAY)}:${cal1.get(Calendar.MINUTE)}")
        dateTextView1.setOnClickListener {
            // Create a DatePicker dialog
            val datePickerDialog = DatePickerDialog(
                this@AddScreening,
                { _, year, monthOfYear, dayOfMonth ->
                    cal1.set(Calendar.YEAR, year)
                    cal1.set(Calendar.MONTH, monthOfYear)
                    cal1.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    // Create a TimePicker dialog
                    val timePickerDialog = TimePickerDialog(
                        this@AddScreening,
                        { _, hourOfDay, minute ->
                            cal1.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            cal1.set(Calendar.MINUTE, minute)

                            val myFormat = "dd/MM/yyyy HH:mm"
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            startTimeET!!.setText(sdf.format(cal1.time))
                        },
                        cal1.get(Calendar.HOUR_OF_DAY),
                        cal1.get(Calendar.MINUTE),
                        false
                    )

                    timePickerDialog.show()
                },
                cal1.get(Calendar.YEAR),
                cal1.get(Calendar.MONTH),
                cal1.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }

        coroutineScope.launch {
            auditoriums = getAuditoriumData()
            movies = getMovieData()
            val auditoriumAdapter = ArrayAdapter(this@AddScreening, R.layout.spinner_item, auditoriums.map{ it.name })
            auditoriumNameSpinner!!.adapter = auditoriumAdapter
            auditoriumNameSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    auditoriumNameSpinner!!.setSelection(position)
                    auditoriumChoice = auditoriums[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            val adapter = ArrayAdapter(this@AddScreening, R.layout.spinner_item, movies.map { it.title })
            movieNameSpinner!!.adapter = adapter
            movieNameSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    movieNameSpinner!!.setSelection(position)
                    movieChoice = movies[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
        saveBtn!!.setOnClickListener {
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
            val start = format.parse(startTimeET!!.text.toString())
            coroutineScope.launch {
                addScreening(
                    auditoriumChoice!!.id, cinema!!.id, movieChoice!!.id,
                    start
                )
            }
        }
    }
    suspend fun addScreening(auditorium_id: String,cinema_id: String,movie_id: String,
                     screening_start: Date) {
        val db = Firebase.firestore

        val result = db.collection("movie")
            .document(movie_id)
            .get()
            .await()
        val movie = result.toObject(Movie::class.java)?: Movie()
        val calendar = Calendar.getInstance()
        calendar.time = screening_start
        calendar.add(Calendar.MINUTE, movie.duration)

        val screening = Screening(
            auditorium_id, cinema_id, movie_id,
            screening_start, calendar.time)
        db.collection("screening")
            .add(screening)
            .addOnSuccessListener {
                val replyIntent = Intent()
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error adding document", e)
            }
    }
    suspend fun getAuditoriumData(): List<Auditorium> = runCatching {
        val db = Firebase.firestore
        val result = db.collection("auditorium")
            .whereEqualTo("is_deleted", false)
            .whereEqualTo("cinema_id", cinema!!.id)
            .get()
            .await()
        result.toObjects(Auditorium::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        emptyList()
    }
    suspend fun getMovieData(): List<Movie> = runCatching {
        val db = Firebase.firestore
        val result = db.collection("movie")
            .whereEqualTo("is_active", true)
            .whereEqualTo("is_deleted", false)
            .get()
            .await()
        result.toObjects(Movie::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        emptyList()
    }
    override fun onDestroy(){
        super.onDestroy()
        coroutineScope.cancel()
    }
}