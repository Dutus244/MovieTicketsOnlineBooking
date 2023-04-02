package com.example.admin.cinemas

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
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class CinemaList : AppCompatActivity() {
    var autoCompleteTextView: AutoCompleteTextView? = null
    var cinemaRecyclerView: RecyclerView? = null
    var addBtn: Button? = null

    var cinemas: List<Cinema> = listOf()

    // Create a CoroutineScope instance in the activity
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema_list)

        autoCompleteTextView = findViewById(R.id.cinemaListACTV)
        cinemaRecyclerView = findViewById(R.id.cinemaListRecyclerView)
        addBtn = findViewById(R.id.cinemaListAddBtn)

        val adapter = CinemaListAdapter(this, cinemas)
        cinemaRecyclerView!!.adapter = adapter
        cinemaRecyclerView!!.layoutManager = LinearLayoutManager(this)

        addBtn!!.setOnClickListener {
            val intent = Intent(this, AddCinema::class.java)
            startActivityForResult(intent, RequestCode.CINEMA_SCREEN_ADD)
        }

        coroutineScope.launch {
            cinemas = getCinemaData()
            cinemaRecyclerView!!.adapter = CinemaListAdapter(this@CinemaList, cinemas)

            val autoCompleteAdapter =
                ArrayAdapter(
                    this@CinemaList,
                    android.R.layout.simple_list_item_single_choice,
                    cinemas.map { it.name })
            autoCompleteTextView!!.setAdapter(autoCompleteAdapter)
            autoCompleteTextView!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    cinemaRecyclerView!!.adapter =
                        CinemaListAdapter(this@CinemaList, cinemas.filter {
                            it.name.contains(autoCompleteTextView!!.text, true)
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

    suspend fun getCinemaData(): List<Cinema> = runCatching {
        FirebaseApp.initializeApp(this@CinemaList)
        val db = Firebase.firestore
        val result = db.collection("cinema")
            .whereEqualTo("is_deleted", false)
            .get()
            .await()
        Log.e("bucu", result.toString())
        result.toObjects(Cinema::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        emptyList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode.CINEMA_SCREEN_ADD || requestCode == RequestCode.CINEMA_SCREEN_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                recreate()
            }
        }
    }
}