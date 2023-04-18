package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.fragments.Item
import com.example.movieticketsonlinebooking.fragments.MyGridAdapter

class FilmListActivity : AppCompatActivity() {
    var arrayList: ArrayList<Item> = ArrayList()
    var adapter : MyGridAdapter? = null
    var gridView: GridView? = null
    var buttonCurrentFilm: Button? = null
    var buttonComingFilm: Button? = null
    var current: Int? = 1

    fun getDataForList () {
        arrayList.clear()
        if (current == 1) {
            // Ds phim hiện lấy all
            arrayList.add(Item("hello",R.drawable.foreplay_background))
            arrayList.add(Item("hello",R.drawable.foreplay_background))
            arrayList.add(Item("hello",R.drawable.foreplay_background))
            arrayList.add(Item("hello",R.drawable.foreplay_background))
            arrayList.add(Item("hello",R.drawable.foreplay_background))
            arrayList.add(Item("hello",R.drawable.foreplay_background))
        }
        else {
            // Ds phim sắp chiếu
            arrayList.add(Item("hi",R.drawable.foreplay_background))
            arrayList.add(Item("hi",R.drawable.foreplay_background))
            arrayList.add(Item("hi",R.drawable.foreplay_background))
            arrayList.add(Item("hi",R.drawable.foreplay_background))
            arrayList.add(Item("hio",R.drawable.foreplay_background))
            arrayList.add(Item("hi",R.drawable.foreplay_background))
        }
        adapter?.notifyDataSetChanged()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_list)

        buttonCurrentFilm = findViewById(R.id.activity_film_list_button_current_film)
        buttonComingFilm = findViewById(R.id.activity_film_list_button_comming_film)

        buttonComingFilm?.setOnClickListener {
            current = 0
            buttonComingFilm?.setTextColor(Color.parseColor("#FF0303"));
            buttonCurrentFilm?.setTextColor(Color.parseColor("#C8C8C8"));
            getDataForList ()
        }

        buttonCurrentFilm?.setOnClickListener {
            current = 1
            buttonComingFilm?.setTextColor(Color.parseColor("#C8C8C8"));
            buttonCurrentFilm?.setTextColor(Color.parseColor("#FF0303"));
            getDataForList ()
        }

        getDataForList ()

        gridView = findViewById(R.id.activity_film_list_gridview_list_film)
        adapter = MyGridAdapter(this, arrayList)
        gridView!!.adapter = adapter
        gridView!!.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(applicationContext, FilmInfoActivity::class.java)
            startActivity(intent)
        }
    }
}