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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class EditScreening : AppCompatActivity() {
    var cinemaNameET: EditText? = null
    var auditoriumNameSpinner: Spinner? = null
    var movieNameSpinner: Spinner? = null
    var startTimeET: EditText? = null
    var endTimeET: EditText? = null
    var delBtn: Button? = null
    var saveBtn: Button? = null

    var screening: Screening? = null

    var auditoriumChoice: String = ""
    var movieChoice: String = ""

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_screening)
        FirebaseApp.initializeApp(this@EditScreening)

        val intent = intent
        screening = intent.getSerializableExtra("screening") as? Screening

        cinemaNameET = findViewById(R.id.editScreeningCinemaNameET)
        auditoriumNameSpinner = findViewById(R.id.editScreeningAuditoriumSpinner)
        movieNameSpinner = findViewById(R.id.editScreeningMovieSpinner)
        startTimeET = findViewById(R.id.editScreeningStartET)
        endTimeET = findViewById(R.id.editScreeningEndET)
        delBtn = findViewById(R.id.editScreeningDeleteBtn)
        saveBtn = findViewById(R.id.editScreeningSaveBtn)

        val dateTextView1 = findViewById<ImageView>(R.id.calendar_icon1)
        val cal1 = Calendar.getInstance()
        startTimeET!!.setText(SimpleDateFormat("dd/MM/yyyy HH:mm",
            Locale.getDefault()).format(screening!!.screening_start))
        dateTextView1.setOnClickListener {
            // Create a DatePicker dialog
            val datePickerDialog = DatePickerDialog(
                this@EditScreening,
                { _, year, monthOfYear, dayOfMonth ->
                    cal1.set(Calendar.YEAR, year)
                    cal1.set(Calendar.MONTH, monthOfYear)
                    cal1.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    // Create a TimePicker dialog
                    val timePickerDialog = TimePickerDialog(
                        this@EditScreening,
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

        val dateTextView2 = findViewById<ImageView>(R.id.calendar_icon2)
        val cal2 = Calendar.getInstance()
        endTimeET!!.setText(SimpleDateFormat("dd/MM/yyyy HH:mm",
            Locale.getDefault()).format(screening!!.screening_end))
        dateTextView2.setOnClickListener {
            // Create a DatePicker dialog
            val datePickerDialog = DatePickerDialog(
                this@EditScreening,
                { _, year, monthOfYear, dayOfMonth ->
                    cal2.set(Calendar.YEAR, year)
                    cal2.set(Calendar.MONTH, monthOfYear)
                    cal2.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    // Create a TimePicker dialog
                    val timePickerDialog = TimePickerDialog(
                        this@EditScreening,
                        { _, hourOfDay, minute ->
                            cal2.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            cal2.set(Calendar.MINUTE, minute)

                            val myFormat = "dd/MM/yyyy HH:mm"
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            endTimeET!!.setText(sdf.format(cal2.time))
                        },
                        cal2.get(Calendar.HOUR_OF_DAY),
                        cal2.get(Calendar.MINUTE),
                        false
                    )

                    timePickerDialog.show()
                },
                cal2.get(Calendar.YEAR),
                cal2.get(Calendar.MONTH),
                cal2.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        coroutineScope.launch {
            val auditoriuIDs = getAuditoriumData().map { it.id }
            val auditoriumNames = getAuditoriumData().map { it.name }
            val auditoriumAdapter = ArrayAdapter(this@EditScreening, R.layout.spinner_item, auditoriumNames)
            auditoriumNameSpinner!!.adapter = auditoriumAdapter
            auditoriumNameSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    auditoriumNameSpinner!!.setSelection(position)
                    auditoriumChoice = auditoriuIDs[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            val movieIDs = getMovieData().map { it.id }
            val movieNames = getMovieData().map { it.title }
            val adapter = ArrayAdapter(this@EditScreening, R.layout.spinner_item, movieNames)
            movieNameSpinner!!.adapter = adapter
            movieNameSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    movieNameSpinner!!.setSelection(position)
                    movieChoice = movieIDs[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            val auditoriumIndex = auditoriuIDs.indexOf(screening!!.auditorium_id)
            if (auditoriumIndex != -1) {
                auditoriumNameSpinner!!.setSelection(auditoriumIndex)
            }
            val movieIndex = movieIDs.indexOf(screening!!.movie_id)
            if (movieIndex != -1) {
                movieNameSpinner!!.setSelection(movieIndex)
            }
            val cinemaIDs = getCinemaData().map { it.id }
            val cinemaNames = getCinemaData().map { it.name }
            val cinemaIndex = cinemaIDs.indexOf(screening!!.cinema_id)
            if (cinemaIndex != -1) {
                cinemaNameET!!.setText(cinemaNames[cinemaIndex])
            }
        }
        saveBtn!!.setOnClickListener {
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
            val start = format.parse(startTimeET!!.text.toString())
            val end = format.parse(endTimeET!!.text.toString())
            Log.e("bucu", auditoriumChoice + " " + movieChoice)
            Log.e("bucu", start.toString() + " " + end.toString())
            editScreening(auditoriumChoice, screening!!.cinema_id, movieChoice,
                start, end
            )
        }
    }
    fun editScreening(auditorium_id: String,cinema_id: String,movie_id: String,
                     screening_start: Date,screening_end: Date) {
        val db = Firebase.firestore
        db.collection("screening")
            .document(screening!!.id).update(
                "auditorium_id", auditorium_id,
                "movie_id", movie_id,
                "screening_start", screening_start,
                "screening_end", screening_end
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
    suspend fun getAuditoriumData(): List<Auditorium> = runCatching {
        val db = Firebase.firestore
        val result = db.collection("auditorium")
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
            .get()
            .await()
        result.toObjects(Movie::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        emptyList()
    }
    suspend fun getCinemaData(): List<Cinema> = runCatching {
        val db = Firebase.firestore
        val result = db.collection("cinema")
            .get()
            .await()
        result.toObjects(Cinema::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        emptyList()
    }
}