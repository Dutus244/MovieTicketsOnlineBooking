package com.example.admin.screenings

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
import com.example.admin.cinemas.AddCinema
import com.example.admin.cinemas.Cinema
import com.example.admin.cinemas.CinemaListAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class CinemaScreeningList : AppCompatActivity() {
    var autoCompleteTextView: AutoCompleteTextView? = null
    var cinemaRecyclerView: RecyclerView? = null

    var cinemas: List<Cinema> = listOf()

    // Create a CoroutineScope instance in the activity
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema_screening_list)

        autoCompleteTextView = findViewById(R.id.cinemaListACTV)
        cinemaRecyclerView = findViewById(R.id.cinemaListRecyclerView)

        val adapter = CinemaScreeningListAdapter(this, cinemas)
        cinemaRecyclerView!!.adapter = adapter
        cinemaRecyclerView!!.layoutManager = LinearLayoutManager(this)

        coroutineScope.launch {
            cinemas = getCinemaData()
            cinemaRecyclerView!!.adapter = CinemaScreeningListAdapter(this@CinemaScreeningList, cinemas)

            val autoCompleteAdapter =
                ArrayAdapter(
                    this@CinemaScreeningList,
                    android.R.layout.simple_list_item_single_choice,
                    cinemas.map { it.name })
            autoCompleteTextView!!.setAdapter(autoCompleteAdapter)
            autoCompleteTextView!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    cinemaRecyclerView!!.adapter =
                        CinemaScreeningListAdapter(this@CinemaScreeningList, cinemas.filter {
                            it.name.contains(autoCompleteTextView!!.text, true)
                        })
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    suspend fun getCinemaData(): List<Cinema> = runCatching {
        FirebaseApp.initializeApp(this@CinemaScreeningList)
        val db = Firebase.firestore
        val result = db.collection("cinema")
            .whereEqualTo("is_deleted", false)
            .get()
            .await()
        result.toObjects(Cinema::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        emptyList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode.SCREENING_SCREEN_DETAIL) {
            if (resultCode == Activity.RESULT_OK) {
                recreate()
            }
        }
    }
}