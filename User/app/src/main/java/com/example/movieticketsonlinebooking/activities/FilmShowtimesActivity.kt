package com.example.movieticketsonlinebooking.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.CinemaScreening
import com.example.movieticketsonlinebooking.viewmodels.Screening
import com.example.movieticketsonlinebooking.viewmodels.UserManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList


class FilmShowtimesActivity : AppCompatActivity(), TextWatcher {
    private var movieInfoBtn: Button? = null

    var movie_id: String? = null
    var screenings: ArrayList<Screening>? = null
    var cinemaScreening: ArrayList<CinemaScreening>? = null

    var dayBtn = arrayOfNulls<Button>(7)

    var tempList: ArrayList<Cinema> = arrayListOf()
    val cinemaList: ArrayList<Cinema> = arrayListOf()
    var adapter: CinemaAdapter? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    class Cinema(val name: String, val showtimes: List<String>)

    fun loginToAccount() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, 1001)
    }

    class CinemaAdapter(private val context: Context, private val cinemaList: ArrayList<Cinema>) :
        BaseAdapter() {

        private val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return cinemaList.size
        }

        override fun getItem(position: Int): Any {
            return cinemaList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val holder: ViewHolder

            if (convertView == null) {
                view = inflater.inflate(R.layout.cinema_list_item, parent, false)
                holder = ViewHolder()
                holder.cinemaName = view.findViewById(R.id.cinema_name)
                holder.showtimesButton = view.findViewById(R.id.showtimes_button)
                holder.showtimesGridView = view.findViewById(R.id.showtimes_gridview)

                view.tag = holder
            } else {
                view = convertView
                holder = convertView.tag as ViewHolder
            }

            val cinema = cinemaList[position]

            holder.cinemaName.text = cinema.name

            holder.showtimesButton.setOnClickListener {
                if (holder.showtimesGridView.visibility == View.VISIBLE) {
                    holder.showtimesGridView.visibility = View.GONE
                } else {
                    holder.showtimesGridView.visibility = View.VISIBLE
                }
            }

            val adapter =
                ArrayAdapter(context, android.R.layout.simple_list_item_1, cinema.showtimes)
            holder.showtimesGridView.adapter = adapter
            holder.showtimesGridView.numColumns = 3

            holder.showtimesGridView.setOnItemClickListener { _, _, position, _ ->
                val intent = Intent(context, BookSeatActivity::class.java)
//                    intent.putExtra("cinemaName", cinema.name)
//                    intent.putExtra("showtime", showtime)
                context.startActivity(intent)
            }
            return view
        }


        private class ViewHolder {
            lateinit var cinemaName: TextView
            lateinit var showtimesButton: Button
            lateinit var showtimesGridView: GridView
        }

        fun updateData(newList: List<Cinema>) {
            cinemaList.clear()
            cinemaList.addAll(newList)
            notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_showtimes)
        FirebaseApp.initializeApp(this@FilmShowtimesActivity)

        movieInfoBtn = findViewById(R.id.activity_film_showtimes_info)
        movieInfoBtn!!.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        dayBtn[0] = findViewById(R.id.button1)
        dayBtn[1] = findViewById(R.id.button2)
        dayBtn[2] = findViewById(R.id.button3)
        dayBtn[3] = findViewById(R.id.button4)
        dayBtn[4] = findViewById(R.id.button5)
        dayBtn[5] = findViewById(R.id.button6)
        dayBtn[6] = findViewById(R.id.button7)

        val currentDate = Date()


        coroutineScope.launch {
            val intent = intent
            movie_id = intent.getStringExtra("movie_id")

            screenings = getScreenings(movie_id!!)
            cinemaScreening = getCinemaScreening(screenings!!)
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

        cinemaList.add(Cinema("Galaxy Nguyễn Du", temp))
        cinemaList.add(Cinema("BHD Quang Trung", temp))
        cinemaList.add(Cinema("BHD Gò Vấp", temp))
        cinemaList.add(Cinema("Cienstar Nguyễn Trãi", temp))
        cinemaList.add(Cinema("Cienstar Hai Bà Trưng", temp))

        tempList.addAll(cinemaList)

        val cinemaListView = findViewById<ListView>(R.id.activity_film_showtimes_list_cinema)
        adapter = CinemaAdapter(this, cinemaList)
        cinemaListView.adapter = adapter

        var searchEditText: EditText = findViewById(R.id.activity_cinema_search_search)
        searchEditText.addTextChangedListener(this);
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    var resultList: ArrayList<Cinema> = ArrayList()

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        resultList.clear()
        if (s != null && s.length > 0) {
            val query = s.toString().uppercase()
            val filters: java.util.ArrayList<Cinema> =
                java.util.ArrayList<Cinema>()
            for (i in 0 until tempList.size) {
                val name: String = tempList.get(i).name
                if (name.uppercase().contains(query)) {
                    filters.add(
                        Cinema(
                            tempList.get(i).name,
                            tempList.get(i).showtimes
                        )
                    )
                }
            }
            resultList.addAll(filters)
        } else {
            for (i in 0 until tempList.size) {
                resultList.add(
                    Cinema(
                        tempList.get(i).name,
                        tempList.get(i).showtimes
                    )
                )
            }
        }
        for (i in resultList) {
            println(i.name)
        }
        adapter!!.updateData(resultList)
    }

    override fun afterTextChanged(s: Editable?) {

    }

    suspend fun getScreenings(movie_id: String): ArrayList<Screening>? = runCatching {
        val db = Firebase.firestore
        val res = arrayListOf<Screening>()

        // Get the current date and time
        val currentDate = Date()

        // Get the next week end of day of current date
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val oneWeekFromNow = calendar.time

        // Construct the query to retrieve documents where screening_start is within a week from now
        val snapshot = db.collection("screening")
            .whereEqualTo("is_deleted", false)
            .whereEqualTo("movie_id", movie_id)
            .whereGreaterThan("screening_start", currentDate)
            .whereLessThan("screening_start", oneWeekFromNow)
            .get()
            .await()
        res.addAll(snapshot.toObjects(Screening::class.java))
        res
    }.getOrElse {
        Log.w("DB", "Error getting data .", it)
        null
    }

    suspend fun getCinemaScreening(screenings: ArrayList<Screening>): ArrayList<CinemaScreening>? =
        runCatching {
            val db = Firebase.firestore
            val uniqueCinemaIds = screenings.distinctBy { it.cinema_id }.map { it.cinema_id }
            val res = arrayListOf<CinemaScreening>()

            for (cinema_id in uniqueCinemaIds) {
                val snapshot = db.collection("cinema")
                    .document(cinema_id)
                    .get()
                    .await()
                val tempCinemaScreening = snapshot.toObject(CinemaScreening::class.java)
                tempCinemaScreening!!.screenings = screenings.filter { it.cinema_id == cinema_id }
                res.add(tempCinemaScreening)
            }
            screenings.clear()
            res
        }.getOrElse {
            Log.w("DB", "Error getting data .", it)
            null
        }
}