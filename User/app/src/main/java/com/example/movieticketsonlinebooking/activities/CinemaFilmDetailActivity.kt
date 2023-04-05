package com.example.movieticketsonlinebooking.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.movieticketsonlinebooking.R

class CinemaFilmDetailActivity : AppCompatActivity() {
    var buttonAddress: Button? = null
    val filmList: ArrayList<Film> =  ArrayList()
    var adapter:FilmAdapter? = null
    class Film(val name: String, val rating: String, val age: String, val time: String, var date: String,  var avatar: Int, val showtimes: List<String>)

    class FilmAdapter(private val context: Context, private val filmList: ArrayList<Film>) : BaseAdapter() {

        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return filmList.size
        }

        override fun getItem(position: Int): Any {
            return filmList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val holder: ViewHolder

            if (convertView == null) {
                view = inflater.inflate(R.layout.film_list_item, parent, false)
                holder = ViewHolder()
                holder.filmName = view.findViewById(R.id.film_name)
                holder.filmRating = view.findViewById(R.id.film_rating)
                holder.filmAge = view.findViewById(R.id.film_age)
                holder.filmTime = view.findViewById(R.id.film_time)
                holder.filmDate = view.findViewById(R.id.film_date)
                holder.filmAvatar = view.findViewById(R.id.film_avatar)
                holder.showtimesButton = view.findViewById(R.id.showtimes_button)
                holder.showtimesGridView = view.findViewById(R.id.showtimes_gridview)

                view.tag = holder
            } else {
                view = convertView
                holder = convertView.tag as ViewHolder
            }

            val film = filmList[position]


            holder.filmName.text = film.name
            holder.filmRating.text = film.rating
            holder.filmAge.text = film.age
            holder.filmTime.text = film.time
            holder.filmDate.text = film.date
            holder.filmAvatar.setImageResource(film.avatar)

            holder.showtimesButton.setOnClickListener {
                if (holder.showtimesGridView.visibility == View.VISIBLE) {
                    holder.showtimesGridView.visibility = View.GONE
                } else {
                    holder.showtimesGridView.visibility = View.VISIBLE
                }
            }

            val adapter = ArrayAdapter(context, R.layout.showtime_grid_item, film.showtimes)
            holder.showtimesGridView.adapter = adapter
            holder.showtimesGridView.numColumns = 3

            // Add OnClickListener to each showtime button
            for (i in 0 until holder.showtimesGridView.childCount) {
                val button = holder.showtimesGridView.getChildAt(i) as Button
                val showtime = film.showtimes[i]

                button.setOnClickListener {
//                    val intent = Intent(context, ShowtimeActivity::class.java)
//                    intent.putExtra("cinemaName", cinema.name)
//                    intent.putExtra("showtime", showtime)
//                    context.startActivity(intent)
                }
            }

            return view
        }
        private class ViewHolder {
            lateinit var filmName: TextView
            lateinit var filmRating: TextView
            lateinit var filmAge: TextView
            lateinit var filmTime: TextView
            lateinit var filmDate: TextView
            lateinit var filmAvatar: ImageView
            lateinit var showtimesButton: Button
            lateinit var showtimesGridView: GridView
        }

        fun updateData(newList: List<Film>) {
            filmList.clear()
            filmList.addAll(newList)
            notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema_film_detail)


        buttonAddress = findViewById(R.id.activity_cinema_film_detail_address)
        buttonAddress?.setOnClickListener {
            val intent = Intent(applicationContext, MapCinemaActivity::class.java)
            startActivity(intent)
        }

        var temp = ArrayList<String>()

        temp.add("7:00-8:30")
        temp.add("8:30-9:00")
        temp.add("9:30-9:00")
        temp.add("10:30-9:00")
        temp.add("10:30-9:00")
        temp.add("10:30-9:00")
        temp.add("10:30-9:00")
        temp.add("10:30-9:00")

        filmList.add(Film("Siêu lừa gặp siêu lầy","8.1","C16","112 Phút","01/03/2023",R.drawable.foreplay_background,temp))
        filmList.add(Film("Biệt Đội Rất Ổn","6.8","C13","104 Phút","01/03/2023",R.drawable.foreplay_background,temp))
        filmList.add(Film("Tri Kỷ","9.2","C16","124 Phút","01/03/2023",R.drawable.foreplay_background,temp))
        filmList.add(Film("Siêu lừa gặp siêu lầy","8.1","C16","112 Phút","01/03/2023",R.drawable.foreplay_background,temp))

        val filmListView = findViewById<ListView>(R.id.activity_cinema_film_detail_film_list_film)
        adapter = FilmAdapter(this, filmList)
        filmListView.adapter = adapter
    }
}