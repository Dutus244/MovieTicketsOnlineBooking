package com.example.admin.cinemas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R

class CinemaList : AppCompatActivity() {
    var autoCompleteTextView: AutoCompleteTextView? = null
    var cinemaRecyclerView: RecyclerView? = null
    var addBtn: Button? = null

    var cinemas = arrayListOf<Cinema>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema_list)

        cinemas.add(Cinema("", "Galaxy", "123", 10, "Normal", false))
        cinemas.add(Cinema("", "Lotte", "456", 5, "Normal", false))
        cinemas.add(Cinema("", "CGV", "789", 20, "Normal", false))

        autoCompleteTextView = findViewById(R.id.cinemaListACTV)
        cinemaRecyclerView = findViewById(R.id.cinemaListRecyclerView)
        addBtn = findViewById(R.id.cinemaListAddBtn)

        val adapter = CinemaListAdapter(this, cinemas)
        cinemaRecyclerView!!.adapter = adapter
        cinemaRecyclerView!!.layoutManager = LinearLayoutManager(this)
    }
}