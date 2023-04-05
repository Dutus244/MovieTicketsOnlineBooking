package com.example.admin.screenings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R.*
import com.example.admin.RequestCode
import com.example.admin.auditoriums.Auditorium
import com.example.admin.cinemas.Cinema
import com.example.admin.movies.Movie
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ScreeningList : AppCompatActivity() {
    var autoCompleteTextView: AutoCompleteTextView? = null
    var screeningRecyclerView: RecyclerView? = null
    var addBtn: Button? = null

    var screenings: List<Screening> = listOf()
    var cinema: Cinema? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_screening_list)
        FirebaseApp.initializeApp(this@ScreeningList)

        val intent = intent
        cinema = intent.getSerializableExtra("cinema") as? Cinema

        autoCompleteTextView = findViewById(id.screeningListACTV)
        screeningRecyclerView = findViewById(id.screeningListRecyclerView)
        addBtn = findViewById(id.screeningListAddBtn)

        val adapter = ScreeningListAdapter(this, screenings)
        screeningRecyclerView!!.adapter = adapter
        screeningRecyclerView!!.layoutManager = LinearLayoutManager(this)

        addBtn!!.setOnClickListener {
            val intent = Intent(this, AddScreening::class.java)
            intent.putExtra("cinema", cinema)
            startActivityForResult(intent, RequestCode.SCREENING_SCREEN_ADD)
        }

        coroutineScope.launch {
            screenings = getScreeningData(cinema!!.id)
            screeningRecyclerView!!.adapter = ScreeningListAdapter(this@ScreeningList, screenings)

            val autoCompleteAdapter =
                ArrayAdapter(
                    this@ScreeningList,
                    android.R.layout.simple_list_item_single_choice,
                    screenings.map { it.movie_name })
            autoCompleteTextView!!.setAdapter(autoCompleteAdapter)
            autoCompleteTextView!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    screeningRecyclerView!!.adapter =
                        ScreeningListAdapter(this@ScreeningList, screenings.filter {
                            it.movie_name.contains(autoCompleteTextView!!.text, true)
                        })
                }
            })
        }
        for (i in screenings){
            Log.e("bucu", i.id + " " + i.movie_name)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    suspend fun getScreeningData(cinema_id: String): List<Screening> = runCatching {
        val db = Firebase.firestore
        val result = db.collection("screening")
            .whereEqualTo("is_deleted", false)
            .whereEqualTo("cinema_id", cinema_id)
            .get()
            .await()
        val movie_name = db.collection("movie")
            .get()
            .await()
        val auditorium_name = db.collection("auditorium")
            .whereEqualTo("cinema_id", cinema_id)
            .get()
            .await()
        var scr: List<Screening> = result.toObjects(Screening::class.java)
        var mv: List<Movie> = movie_name.toObjects(Movie::class.java)
        var au: List<Auditorium> = auditorium_name.toObjects(Auditorium::class.java)
        for(i in scr) {
            for(j in mv) {
                if(i.movie_id.equals(j.id))
                    i.movie_name = j.title
            }
        }
        for(i in scr) {
            for(j in au) {
                if(i.auditorium_id.equals(j.id))
                    i.auditorium_name = j.name
            }
        }
        scr
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        emptyList()
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