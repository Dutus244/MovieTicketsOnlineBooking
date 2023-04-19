package com.example.admin.reviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.movies.Movie
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MovieReviewList : AppCompatActivity() {
    var autoCompleteTextView: AutoCompleteTextView? = null
    var movieRecyclerView: RecyclerView? = null

    var movies: List<Movie> = listOf()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_review_list)

        autoCompleteTextView = findViewById(R.id.movieListACTV)
        movieRecyclerView = findViewById(R.id.movieListRecyclerView)

        val adapter = MovieReviewListAdapter(this, movies)
        movieRecyclerView!!.adapter = adapter
        movieRecyclerView!!.layoutManager = LinearLayoutManager(this)

        coroutineScope.launch {
            movies = getMovieData()
            movieRecyclerView!!.adapter = MovieReviewListAdapter(this@MovieReviewList, movies)

            val autoCompleteAdapter =
                ArrayAdapter(
                    this@MovieReviewList,
                    android.R.layout.simple_list_item_single_choice,
                    movies.map { it.title })
            autoCompleteTextView!!.setAdapter(autoCompleteAdapter)
            autoCompleteTextView!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    movieRecyclerView!!.adapter =
                        MovieReviewListAdapter(this@MovieReviewList, movies.filter {
                            it.title.contains(autoCompleteTextView!!.text, true)
                        })
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    suspend fun getMovieData(): List<Movie> = runCatching {
        FirebaseApp.initializeApp(this@MovieReviewList)
        val db = Firebase.firestore
        val result = db.collection("movie")
            .whereEqualTo("is_deleted", false)
            .get()
            .await()
        result.toObjects(Movie::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        emptyList()
    }
}