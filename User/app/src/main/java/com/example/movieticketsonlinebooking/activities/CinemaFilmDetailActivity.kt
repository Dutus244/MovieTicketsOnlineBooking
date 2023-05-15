package com.example.movieticketsonlinebooking.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CinemaFilmDetailActivity : AppCompatActivity() {
    var buttonAddress: Button? = null
    var adapter:FilmAdapter? = null
    var cinema: Cinema? = null

    var cinemaName: TextView? = null

    var dateBtn = arrayOfNulls<Button>(7)
    var dateList = arrayOfNulls<Date>(7)
    var activeDateBtn: Button? = null
    var currentDateTV: TextView? = null

    var cinema_id: String? = null
    var screenings: ArrayList<Screening>? = null
    var movieScreening: ArrayList<MovieScreening>? = null
    var activeMovieScreening: ArrayList<MovieScreening>? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    class FilmAdapter(private val context: Context, private val filmList: ArrayList<MovieScreening>, private val cinema: Cinema, private val date: String) : BaseAdapter() {

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

            val film = filmList[position]

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

            holder.filmName.text = film.title


            val decimalFormat = DecimalFormat("#.#")
            decimalFormat.maximumFractionDigits = 1
            val formattedRating = decimalFormat.format(film.rating)
            holder.filmRating.text = formattedRating
            
            holder.filmAge.text = film.classification
            when (film.classification) {
                "P" -> {
                    holder.filmAge.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.movieClassification_P
                        )
                    )
                }
                "C13" -> {
                    holder.filmAge.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.movieClassification_C13
                        )
                    )
                }
                "C16" -> {
                    holder.filmAge.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.movieClassification_C16
                        )
                    )
                }
                "C18" -> {
                    holder.filmAge.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.movieClassification_C18
                        )
                    )
                }
            }


            holder.filmTime.text = film.duration.toString() + " phút"
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val releaseDate = dateFormat.format(film.release_date)
            holder.filmDate.text = releaseDate

            if (!film.poster_url.isEmpty()) {
                Picasso.get().load(film.poster_url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.filmAvatar)
            }

            holder.showtimesButton.setOnClickListener {
                if (holder.showtimesGridView.visibility == View.VISIBLE) {
                    holder.showtimesGridView.visibility = View.GONE
                } else {
                    holder.showtimesGridView.visibility = View.VISIBLE
                }
            }

            val showtimes = ArrayList<String>()
            for (i in film.screenings!!) {
                val start = Calendar.getInstance().apply { time = i.screening_start }
                val end = Calendar.getInstance().apply { time = i.screening_end }

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                showtimes.add(timeFormat.format(start.time) + " - " + timeFormat.format(end.time))
            }

            val adapter =
                ArrayAdapter(context, android.R.layout.simple_list_item_1, showtimes)
            holder.showtimesGridView.adapter = adapter
            holder.showtimesGridView.numColumns = 3

            holder.showtimesGridView.setOnItemClickListener { _, _, position, _ ->
                if (UserManager.isLoggedIn()) {
                    if (cinema.type == "Big") {
                        val intent = Intent(context, BookSeatActivity::class.java)
                        intent.putExtra("cinema_name", cinema.name)
                        intent.putExtra("movie_title", film.title)
                        intent.putExtra("date", date)
                        intent.putExtra("price", cinema.price)
                        intent.putExtra(
                            "screenings",
                            ArrayList(film.screenings!!)
                        )
                        intent.putExtra("screeningSelectedPos", position)
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(context, BookSmallCinemaActivity::class.java)
                        intent.putExtra("cinema", cinema)
                        intent.putExtra("cinema_name", cinema.name)
                        intent.putExtra("movie_title", film.title)
                        intent.putExtra("date", date)
                        intent.putExtra(
                            "screenings",
                            ArrayList(film.screenings!!)
                        )
                        intent.putExtra("screeningSelectedPos", position)
                        context.startActivity(intent)
                    }
                } else {
                    val loginOrSignupDialog = AlertDialog.Builder(context)
                        .setTitle("Yêu cầu đăng nhập")
                        .setMessage("Bạn phải đăng nhập để đặt vé phim. Bạn có muốn đăng nhập hoặc đăng ký?")
                        .setPositiveButton("Đăng nhập") { _, _ ->
                            // Show login activity
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                        }
                        .setNegativeButton("Đăng ký") { _, _ ->
                            // Show signup activity
                            val intent = Intent(context, SignupActivity1::class.java)
                            context.startActivity(intent)
                        }
                        .setNeutralButton("Hủy", null)
                        .create()
                    loginOrSignupDialog.show()
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

        fun updateData(newList: List<MovieScreening>) {
            filmList.clear()
            filmList.addAll(newList)
            notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema_film_detail)

        intent = intent
        cinema = intent.getSerializableExtra("cinema") as Cinema

        cinemaName = findViewById(R.id.activity_cinema_film_detail_film_name)
        cinemaName!!.text = cinema!!.name


        buttonAddress = findViewById(R.id.activity_cinema_film_detail_address)
        buttonAddress!!.text = cinema!!.address
        buttonAddress?.setOnClickListener {
            val intent = Intent(applicationContext, MapCinemaActivity::class.java)
            intent.putExtra("cinema", cinema)
            startActivity(intent)
        }

        dateBtn[0] = findViewById(R.id.button1)
        dateBtn[1] = findViewById(R.id.button2)
        dateBtn[2] = findViewById(R.id.button3)
        dateBtn[3] = findViewById(R.id.button4)
        dateBtn[4] = findViewById(R.id.button5)
        dateBtn[5] = findViewById(R.id.button6)
        dateBtn[6] = findViewById(R.id.button7)
        currentDateTV = findViewById(R.id.activity_film_showtimes_time)

        activeDateBtn = dateBtn[0]
        activeDateBtn!!.setBackgroundColor(Color.parseColor("#FF0303"))
        activeDateBtn!!.setTextColor(Color.WHITE)

        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        val calendar = Calendar.getInstance()
        for (i in 0 until 7) {
            val date = calendar.time
            dateList[i] = calendar.time
            dateBtn[i]!!.text = dateFormat.format(date)
            calendar.add(Calendar.DATE, 1)
            dateBtn[i]!!.setOnClickListener { btn ->
                activeDateBtn!!.setBackgroundColor(Color.TRANSPARENT)
                activeDateBtn!!.setTextColor(Color.BLACK)
                (btn as Button).setBackgroundColor(Color.parseColor("#FF0303"))
                btn.setTextColor(Color.WHITE)

                if (btn != activeDateBtn) {
                    currentDateTV!!.text =
                        SimpleDateFormat(
                            "'Ngày' dd 'tháng' MM 'năm' yyyy",
                            Locale.getDefault()
                        ).format(dateList[i]!!)

//                    // Get current date screenings
                    for (j in 0 until activeMovieScreening!!.size) {
                        activeMovieScreening!![j].screenings =
                            movieScreening!![j].screenings!!.filter {
                                isSameDate(it.screening_start, dateList[i]!!)
                            }
                    }
                    adapter!!.updateData(activeMovieScreening!!)
                }
                activeDateBtn = btn
            }
        }
        currentDateTV!!.text =
            SimpleDateFormat(
                "'Ngày' dd 'tháng' MM 'năm' yyyy",
                Locale.getDefault()
            ).format(dateList[0]!!)


        val filmListView = findViewById<ListView>(R.id.activity_cinema_film_detail_film_list_film)
        adapter = FilmAdapter(this, ArrayList(), cinema!!, activeDateBtn!!.text.toString())
        filmListView.adapter = adapter

        coroutineScope.launch {
            cinema_id = intent.getStringExtra("cinema_id")

            screenings = getScreenings(cinema_id!!)
            movieScreening = getMovieScreening(screenings!!)
            // Make a deep copy
            activeMovieScreening =
                movieScreening?.map {
                    it.copy(screenings = ArrayList(it.screenings!!))
                } as ArrayList<MovieScreening>?
            // Get current date screenings
            for (i in activeMovieScreening!!) {
                i.screenings = i.screenings!!.filter {
                    isSameDate(it.screening_start, dateList[0]!!)
                }
            }
            adapter!!.updateData(activeMovieScreening!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    fun isSameDate(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance().apply {
            time = date1
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calendar2 = Calendar.getInstance().apply {
            time = date2
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar1.time == calendar2.time
    }

    suspend fun getScreenings(cinema_id: String): ArrayList<Screening>? = runCatching {
        val db = Firebase.firestore
        val res = arrayListOf<Screening>()

        // Get the current date and time
        val currentDate = Date()

        // Get the next week end of day of current date
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val oneWeekFromNow = calendar.time

        // Construct the query to retrieve documents where screening_start is within a week from now
        val snapshot = db.collection("screening")
            .whereEqualTo("is_deleted", false)
            .whereEqualTo("cinema_id", cinema_id)
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

    suspend fun getMovieScreening(screenings: ArrayList<Screening>): ArrayList<MovieScreening>? =
        runCatching {
            val db = Firebase.firestore
            val uniqueMovieIds = screenings.distinctBy { it.movie_id }.map { it.movie_id }
            val res = arrayListOf<MovieScreening>()

            for (movie_id in uniqueMovieIds) {
                val snapshot = db.collection("movie")
                    .document(movie_id)
                    .get()
                    .await()
                val tempMovieScreening = snapshot.toObject(MovieScreening::class.java)
                tempMovieScreening!!.screenings = screenings.filter { it.movie_id == movie_id }
                res.add(tempMovieScreening)
            }
            screenings.clear()
            res
        }.getOrElse {
            Log.w("DB", "Error getting data .", it)
            null
        }
}