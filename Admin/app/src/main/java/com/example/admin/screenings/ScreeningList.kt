package com.example.admin.screenings

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R.*
import com.example.admin.RequestCode
import com.example.admin.cinemas.Cinema
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*


class ScreeningList : AppCompatActivity() {
    var cinemaNameScreeningScreenTV: TextView? = null
    var cinemaAddressScreeningScreenTV: TextView? = null
    var screeningRecyclerView: RecyclerView? = null
    var addBtn: Button? = null

    var movieScreenings: List<MovieScreening> = listOf()
    var displayMovieScreenings: List<MovieScreening> = listOf()
    var cinema: Cinema? = null

    var selectedTextView: TextView? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_screening_list)
        FirebaseApp.initializeApp(this@ScreeningList)

        val intent = intent
        cinema = intent.getSerializableExtra("cinema") as? Cinema

        cinemaNameScreeningScreenTV = findViewById(id.cinemaNameScreeningScreenTV)
        cinemaAddressScreeningScreenTV = findViewById(id.cinemaAddressScreeningScreenTV)
        screeningRecyclerView = findViewById(id.screeningListRecyclerView)
        addBtn = findViewById(id.screeningListAddBtn)

        cinemaNameScreeningScreenTV!!.setText(cinema!!.name)
        cinemaAddressScreeningScreenTV!!.setText(cinema!!.address)

        val adapter = ScreeningListAdapter(this, movieScreenings)
        screeningRecyclerView!!.adapter = adapter
        screeningRecyclerView!!.layoutManager = LinearLayoutManager(this)

        addBtn!!.setOnClickListener {
            val intent = Intent(this, AddScreening::class.java)
            intent.putExtra("cinema", cinema)
            startActivityForResult(intent, RequestCode.SCREENING_SCREEN_ADD)
        }

        coroutineScope.launch {
            var currentDate = Calendar.getInstance().time

            movieScreenings = getScreeningData(cinema!!.id)
            initializeDisplayList()
            filterScreening(currentDate)
            screeningRecyclerView!!.adapter = ScreeningListAdapter(this@ScreeningList, displayMovieScreenings)

            val sdf = SimpleDateFormat("dd/MM")

            val layout = findViewById<LinearLayout>(id.scrollViewLinearLayout)

            for (i in 0 until 7) {
                val textView = TextView(this@ScreeningList)
                textView.text = sdf.format(currentDate.time)

                val layoutParams = LinearLayout.LayoutParams(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        50F,
                        resources.displayMetrics
                    ).toInt(),
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        50F,
                        resources.displayMetrics
                    ).toInt()
                )
                if (i == 0) {
                    textView.setTextColor(Color.WHITE)
                    textView.setBackgroundColor(Color.parseColor("#673AB7"))
                    selectedTextView = textView
                }
                textView.gravity = Gravity.CENTER
                textView.layoutParams = layoutParams

                val dateCalendar = Calendar.getInstance()
                dateCalendar.time = currentDate

                textView.setOnClickListener {
                    val clickedTextView = it as TextView
                    if (selectedTextView != null) {
                        selectedTextView!!.setTextColor(Color.BLACK)
                        selectedTextView!!.setBackgroundColor(Color.WHITE)
                    }
                    clickedTextView.setTextColor(Color.WHITE)
                    clickedTextView.setBackgroundColor(Color.parseColor("#673AB7"))
                    selectedTextView = clickedTextView

                    val selectedDate = dateCalendar.time
                    filterScreening(selectedDate)
                    screeningRecyclerView!!.adapter?.notifyDataSetChanged()
                }
                layout.addView(textView)

                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                currentDate = calendar.time
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    suspend fun getScreeningData(cinema_id: String): List<MovieScreening> = runCatching {
        val db = Firebase.firestore

        val screeningsQuery = db.collection("screening")
            .whereEqualTo("cinema_id", cinema_id)
            .whereEqualTo("is_deleted", false)
            .get()
            .await()
        val matchingScreenings = screeningsQuery.toObjects(Screening::class.java)

        val moviesQuery = db.collection("movie")
            .whereEqualTo("is_active", true)
            .whereEqualTo("is_deleted", false)
            .get()
            .await()
        val movies = moviesQuery.toObjects(MovieScreening::class.java).map { movie ->
            val movieScreeningsList = matchingScreenings.filter { it.movie_id == movie.id }
                .sortedBy { it.screening_start }
            MovieScreening(
                title = movie.title,
                cast = movie.cast,
                director = movie.director,
                poster_url = movie.poster_url,
                vid_url = movie.vid_url,
                classification = movie.classification,
                release_date = movie.release_date,
                duration = movie.duration,
                description = movie.description,
                rating = movie.rating,
                is_active = movie.is_active,
                is_deleted = movie.is_deleted,
                screenings = movieScreeningsList,
                id = movie.id
            )
        }
        movies
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        emptyList()
    }

    fun initializeDisplayList() {
        displayMovieScreenings = emptyList()
        movieScreenings.map{ movieScreening ->
            displayMovieScreenings = displayMovieScreenings + movieScreening.copy()
        }
    }

    fun filterScreening(selectDate: Date) {
        displayMovieScreenings.map{ movieScreening ->
            movieScreening.screenings = emptyList()
            var movie = movieScreenings.firstOrNull { it.id == movieScreening.id }
            movie!!.screenings.map{screening ->
                if(screening.screening_start.day == selectDate.day &&
                    screening.screening_start.month == selectDate.month)
                    movieScreening.screenings = movieScreening.screenings + screening
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode.SCREENING_SCREEN_ADD || requestCode == RequestCode.SCREENING_SCREEN_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                recreate()
            }
        }
    }
}