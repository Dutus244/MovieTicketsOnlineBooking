package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridView
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.fragments.MyGridAdapter
import com.example.movieticketsonlinebooking.viewmodels.Movie
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class FilmListActivity : AppCompatActivity() {
    var adapter : MyGridAdapter? = null
    var gridView: GridView? = null
    var buttonCurrentFilm: Button? = null
    var buttonComingFilm: Button? = null
    var current: Int? = 1

    var currentMovies: MutableList<Movie> = listOf<Movie>().toMutableList()
    var upcomingMovies: MutableList<Movie> = listOf<Movie>().toMutableList()

    fun getDataForList() {
        // Ds phim hiện tại 6 phần tử
        val db = Firebase.firestore
        db.collection("movie")
            .whereEqualTo("is_active", true)
            .whereEqualTo("is_deleted", false)
            .whereLessThan("release_date", Date())
            .orderBy("release_date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                this.runOnUiThread {
                    currentMovies.addAll(documents.toObjects(Movie::class.java))
                    adapter?.updateData(currentMovies)
                }
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error getting document", e)
            }
        // Ds phim sắp chiếu
        db.collection("movie")
            .whereEqualTo("is_active", true)
            .whereEqualTo("is_deleted", false)
            .whereGreaterThan("release_date", Date())
            .orderBy("release_date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                this.runOnUiThread {
                    upcomingMovies.addAll(documents.toObjects(Movie::class.java))
                }
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error getting document", e)
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_list)

        buttonCurrentFilm = findViewById(R.id.activity_film_list_button_current_film)
        buttonComingFilm = findViewById(R.id.activity_film_list_button_comming_film)

        getDataForList ()

        buttonComingFilm?.setOnClickListener {
            current = 0
            buttonComingFilm?.setTextColor(Color.parseColor("#FF0303"));
            buttonCurrentFilm?.setTextColor(Color.parseColor("#C8C8C8"));
            adapter?.updateData(upcomingMovies)
        }

        buttonCurrentFilm?.setOnClickListener {
            current = 1
            buttonComingFilm?.setTextColor(Color.parseColor("#C8C8C8"));
            buttonCurrentFilm?.setTextColor(Color.parseColor("#FF0303"));
            adapter?.updateData(currentMovies)
        }

        gridView = findViewById(R.id.activity_film_list_gridview_list_film)
        adapter = MyGridAdapter(this, currentMovies)
        gridView!!.adapter = adapter
        gridView!!.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(applicationContext, FilmInfoActivity::class.java)
            if (current == 1) {
                intent.putExtra("movie", currentMovies[i])
            } else if (current == 0) {
                intent.putExtra("movie", upcomingMovies[i])
            }
            startActivity(intent)
        }
    }
}