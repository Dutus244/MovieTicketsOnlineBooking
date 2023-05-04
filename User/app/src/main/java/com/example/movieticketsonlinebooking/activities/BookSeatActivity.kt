package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.Auditorium
import com.example.movieticketsonlinebooking.viewmodels.Reservation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.jahidhasanco.seatbookview.SeatBookView
import dev.jahidhasanco.seatbookview.SeatClickListener
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat
import java.text.NumberFormat

class BookSeatActivity : AppCompatActivity() {
    private var buttonConfirm: Button? = null
    private lateinit var seatBookView: SeatBookView

    private var cinema_name: String? = null
    private var movie_title: String? = null
    private var movie_classification: String? = null
    private var auditorium_id: String? = null
    private var screening_id: String? = null
    private var seatPrice: Int? = null

    private var cinemaNameTV: TextView? = null
    private var movieTitleTV: TextView? = null
    private var movieClassificationTV: TextView? = null
    private var auditoriumNameTV: TextView? = null
    private var priceTV: TextView? = null

    private var auditorium: Auditorium? = null
    private var reservations: ArrayList<Reservation>? = null
    private var bookedSeat: ArrayList<Int>? = null
    private var selectedSeats: List<Int>? = null
    private var totalPrice: Int = 0
    private val selectedSeatsName = object : HashSet<String>() {
        override fun add(element: String): Boolean {
            return if (contains(element)) {
                remove(element)
                true
            } else {
                super.add(element)
            }
        }
    }


    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_seat)

        val intent = intent
        cinema_name = intent.getStringExtra("cinema_name")
        movie_title = intent.getStringExtra("movie_title")
        movie_classification = intent.getStringExtra("movie_classification")
        auditorium_id = intent.getStringExtra("auditorium_id")
        screening_id = intent.getStringExtra("screening_id")
        seatPrice = intent.getIntExtra("price", 0)

        cinemaNameTV = findViewById(R.id.activity_book_seat_cinema_name)
        movieTitleTV = findViewById(R.id.activity_book_seat_film_name)
        movieClassificationTV = findViewById(R.id.activity_film_info_textview_age)
        auditoriumNameTV = findViewById(R.id.activity_film_info_textview_audi_name)
        priceTV = findViewById(R.id.activity_film_info_price)

        cinemaNameTV!!.text = cinema_name
        movieTitleTV!!.text = movie_title
        movieClassificationTV!!.text = movie_classification
        when (movie_classification) {
            "P" -> {
                movieClassificationTV!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.movieClassification_P
                    )
                )
            }
            "C13" -> {
                movieClassificationTV!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.movieClassification_C13
                    )
                )
            }
            "C16" -> {
                movieClassificationTV!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.movieClassification_C16
                    )
                )
            }
            "C18" -> {
                movieClassificationTV!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.movieClassification_C18
                    )
                )
            }
        }

        buttonConfirm = findViewById(R.id.activity_book_seat_button_confirm)
        buttonConfirm?.setOnClickListener {
            val payIntent = Intent(applicationContext, PayActivity::class.java)
            intent.putStringArrayListExtra("selectedSeatsName", ArrayList(selectedSeatsName))
            intent.putIntegerArrayListExtra("selectedSeats", ArrayList(selectedSeats!!))
            intent.putExtra("totalPrice", totalPrice)
            startActivity(payIntent)
        }

        coroutineScope.launch {
            auditorium = getAuditorium(auditorium_id!!)
            auditoriumNameTV!!.text = auditorium!!.name
            reservations = getReservations(screening_id!!)
            bookedSeat = getBookedSeats(reservations!!)

            val titles = makeExistedTitles(auditorium!!.map)
            val seats = makeExistedSeats(auditorium!!.map, bookedSeat!!)
            val pureTitles = titles.filter { it != "/" }
            seatBookView = findViewById(R.id.layoutSeat)
            seatBookView.setSeatsLayoutString(seats)
                .isCustomTitle(true)
                .setCustomTitle(titles)
                .setSeatSize(300)
            seatBookView.show()
            seatBookView.setSeatClickListener(object : SeatClickListener {
                override fun onAvailableSeatClick(selectedIdList: List<Int>, view: View) {
                    val seat = view.findViewById<TextView>(view.id)
                    val seatName = seat.text.toString()

                    selectedSeatsName.add(seatName)
                    selectedSeats = selectedIdList.map { it - 1 }
                    totalPrice = selectedIdList.size * seatPrice!!
                    priceTV!!.text = toVND(totalPrice)
                }

                override fun onBookedSeatClick(view: View) {

                }

                override fun onReservedSeatClick(view: View) {
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun getBookedSeats(reservation: ArrayList<Reservation>): ArrayList<Int> {
        val res = arrayListOf<Int>()
        reservation.forEachIndexed { reservationIdx, reservationItem ->
            res.addAll(reservationItem.seats)
        }
        return res
    }

    private suspend fun getReservations(screening_id: String): ArrayList<Reservation>? = runCatching {
        val db = Firebase.firestore
        val result: ArrayList<Reservation> = arrayListOf()

        val snapShot = db.collection("reservation")
            .whereEqualTo("screening_id", screening_id)
            .get()
            .await()
        result.addAll(snapShot.toObjects(Reservation::class.java))
        result
    }.getOrElse {
        Log.w("DB", "Error getting data .", it)
        null
    }

    private suspend fun getAuditorium(auditorium_id: String): Auditorium? = runCatching {
        val db = Firebase.firestore
        val result = db.collection("auditorium")
            .document(auditorium_id)
            .get()
            .await()
        result.toObject(Auditorium::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        null
    }

    private fun makeExistedSeats(
        map: ArrayList<String>,
        bookedSeats: ArrayList<Int>,
    ): String {
        val mapColNum = map[0].length
        var seats = ""
        // Seat map is like this ["111", "111", "111"]
        map.forEachIndexed { sIndex, s ->
            seats += "/"
            seats += s.mapIndexed { cIndex, c ->
                if (c == '1') {
                    if (sIndex * mapColNum + cIndex in bookedSeats) {
                        "U"
                    } else {
                        "A"
                    }
                } else "_"
            }.joinToString("")
        }
        return seats
    }

    private fun makeExistedTitles(map: ArrayList<String>): List<String> {
        val title = arrayListOf<String>()
        var rowTitle = 'A'
        var count = 0
        for (item in map) {
            var colTitle = 1
            count++
            title.add("/")
            for (i in 1..item.length) {
                count++
                if (item[i - 1] == '1') {
                    title.add(rowTitle + (colTitle++).toString())
                } else
                    title.add("")
            }
            ++rowTitle
        }
        return title
    }

    private fun toVND(num: Int): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return formatter.format(num) + "đ"
    }
}