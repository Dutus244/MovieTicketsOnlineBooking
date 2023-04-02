package com.example.admin.movies

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.RequestCode
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MovieList : AppCompatActivity() {
    var autoCompleteTextView: AutoCompleteTextView? = null
    var movieRecyclerView: RecyclerView? = null
    var addBtn: Button? = null

    var movies: List<Movie> = listOf()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        autoCompleteTextView = findViewById(R.id.movieListACTV)
        movieRecyclerView = findViewById(R.id.movieListRecyclerView)
        addBtn = findViewById(R.id.movieListAddBtn)

        val adapter = MovieListAdapter(this, movies)
        movieRecyclerView!!.adapter = adapter
        movieRecyclerView!!.layoutManager = LinearLayoutManager(this)

        addBtn!!.setOnClickListener {
            val intent = Intent(this, AddMovie::class.java)
            startActivityForResult(intent, RequestCode.MOVIE_SCREEN_ADD)
        }

        coroutineScope.launch {
            movies = getMovieData()
            movieRecyclerView!!.adapter = MovieListAdapter(this@MovieList, movies)

            val autoCompleteAdapter =
                ArrayAdapter(
                    this@MovieList,
                    android.R.layout.simple_list_item_single_choice,
                    movies.map { it.title })
            autoCompleteTextView!!.setAdapter(autoCompleteAdapter)
            autoCompleteTextView!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    movieRecyclerView!!.adapter =
                        MovieListAdapter(this@MovieList, movies.filter {
                            it.title.contains(autoCompleteTextView!!.text, true)
                        })
                }
            })
        }
    }


    // Cancel the coroutine scope when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    suspend fun getMovieData(): List<Movie> = runCatching {
        FirebaseApp.initializeApp(this@MovieList)
        val db = Firebase.firestore
        val result = db.collection("movie")
            .whereEqualTo("is_deleted", false)
            .get()
            .await()
        Log.e("bucu", result.toString())
        result.toObjects(Movie::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        emptyList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode.MOVIE_SCREEN_ADD || requestCode == RequestCode.MOVIE_SCREEN_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                recreate()
            }
        }
    }
}