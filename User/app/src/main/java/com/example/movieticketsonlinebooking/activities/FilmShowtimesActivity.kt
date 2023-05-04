package com.example.movieticketsonlinebooking.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.CinemaScreening
import com.example.movieticketsonlinebooking.viewmodels.Screening
import com.example.movieticketsonlinebooking.viewmodels.UserManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FilmShowtimesActivity : AppCompatActivity(), TextWatcher {
    private var movieInfoBtn: Button? = null

    var movie_id: String? = null
    var movie_title: String? = null
    var movie_classification: String? = null
    var screenings: ArrayList<Screening>? = null
    var cinemaScreening: ArrayList<CinemaScreening>? = null
    var activeCinemaScreening: ArrayList<CinemaScreening>? = null

    var movieNameTV: TextView? = null
    var dateBtn = arrayOfNulls<Button>(7)
    var dateList = arrayOfNulls<Date>(7)
    var activeDateBtn: Button? = null
    var currentDateTV: TextView? = null

    var adapter: CinemaAdapter? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun loginToAccount() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, 1001)
    }

    class CinemaAdapter(
        private val context: Context,
        private val cinemaList: ArrayList<CinemaScreening>,
        private val movie_title: String,
        private val movie_classification: String,
    ) :
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

        private class ViewHolder {
            lateinit var cinemaName: TextView
            lateinit var showtimesButton: Button
            lateinit var showtimesGridView: GridView
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
                    holder.showtimesButton.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_left_24)
                } else {
                    holder.showtimesGridView.visibility = View.VISIBLE
                    holder.showtimesButton.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }

            // Convert Date to HH:mm
            val showtimes = ArrayList<String>()
            for (i in cinema.screenings!!) {
                val start = Calendar.getInstance().apply { time = i.screening_start }
                val end = Calendar.getInstance().apply { time = i.screening_end }

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                showtimes.add(timeFormat.format(start.time) + " - " + timeFormat.format(end.time))
            }


            val adapter =
                ArrayAdapter(context, android.R.layout.simple_list_item_1, showtimes)
            holder.showtimesGridView.adapter = adapter
            holder.showtimesGridView.numColumns = 3

            holder.showtimesGridView.setOnItemClickListener { _, _, screeningPos, _ ->


                if (!UserManager.isLoggedIn()) {
                    if (cinemaList[position].type == "Big") {
                        val intent = Intent(context, BookSeatActivity::class.java)
                        intent.putExtra("cinema_name", cinemaList[position].name)
                        intent.putExtra("movie_title", movie_title)
                        intent.putExtra("price", cinemaList[position].price)
                        intent.putExtra("movie_classification", movie_classification)
                        intent.putExtra(
                            "auditorium_id",
                            cinemaList[position].screenings!![screeningPos].auditorium_id
                        )
                        intent.putExtra(
                            "screening_id",
                            cinemaList[position].screenings!![screeningPos].id
                        )
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(context, BookSeatActivity::class.java)
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

        fun updateData(newList: List<CinemaScreening>) {
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

        val intent = intent
        movie_id = intent.getStringExtra("movie_id")
        movie_title = intent.getStringExtra("movie_title")
        movie_classification = intent.getStringExtra("movie_classification")


        movieNameTV = findViewById(R.id.activity_film_showtimes_film_name)
        movieNameTV!!.text = movie_title
        movieInfoBtn = findViewById(R.id.activity_film_showtimes_info)
        movieInfoBtn!!.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

                    // Get current date screenings
                    for (j in 0 until activeCinemaScreening!!.size) {
                        activeCinemaScreening!![j].screenings =
                            cinemaScreening!![j].screenings!!.filter {
                                isSameDate(it.screening_start, dateList[i]!!)
                            }
                    }
                    adapter!!.updateData(activeCinemaScreening!!)
                }
                activeDateBtn = btn
            }
        }
        currentDateTV!!.text =
            SimpleDateFormat(
                "'Ngày' dd 'tháng' MM 'năm' yyyy",
                Locale.getDefault()
            ).format(dateList[0]!!)

        val cinemaListView = findViewById<ListView>(R.id.activity_film_showtimes_list_cinema)
        adapter = CinemaAdapter(this, ArrayList(), movie_title!!, movie_classification!!)
        cinemaListView.adapter = adapter

        val searchEditText: EditText = findViewById(R.id.activity_cinema_search_search)
        searchEditText.addTextChangedListener(this)

        coroutineScope.launch {
            screenings = getScreenings(movie_id!!)
            cinemaScreening = getCinemaScreening(screenings!!)
            // Make a deep copy
            activeCinemaScreening =
                cinemaScreening?.map {
                    it.copy(screenings = ArrayList(it.screenings!!))
                } as ArrayList<CinemaScreening>?
            // Get current date screenings
            for (i in activeCinemaScreening!!) {
                i.screenings = i.screenings!!.filter {
                    isSameDate(it.screening_start, dateList[0]!!)
                }
            }
            adapter!!.updateData(activeCinemaScreening!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        adapter!!.updateData(activeCinemaScreening!!.filter {
            it.name.contains(s.toString(), ignoreCase = true)
        })
    }

    override fun afterTextChanged(s: Editable?) {

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

    suspend fun getScreenings(movie_id: String): ArrayList<Screening>? = runCatching {
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
                if (tempCinemaScreening!!.status == "Open") {
                    tempCinemaScreening.screenings = screenings.filter { it.cinema_id == cinema_id }
                    res.add(tempCinemaScreening)
                }
            }
            screenings.clear()
            res
        }.getOrElse {
            Log.w("DB", "Error getting data .", it)
            null
        }
}