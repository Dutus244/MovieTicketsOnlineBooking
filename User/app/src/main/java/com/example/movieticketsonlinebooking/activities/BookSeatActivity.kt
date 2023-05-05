package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.Auditorium
import com.example.movieticketsonlinebooking.viewmodels.Reservation
import com.example.movieticketsonlinebooking.viewmodels.Screening
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.jahidhasanco.seatbookview.SeatBookView
import dev.jahidhasanco.seatbookview.SeatClickListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class BookSeatActivity : AppCompatActivity() {
    private var buttonConfirm: Button? = null
    private lateinit var seatBookView: SeatBookView

    private var cinema_name: String? = null
    private var movie_title: String? = null
    private var date: String? = null
    private var auditorium_id: String? = null
    private var screening_id: String? = null
    private var seatPrice: Int? = null
    private var screenings: ArrayList<Screening>? = null
    private var screeningSelectedPos: Int = 0

    private var cinemaNameTV: TextView? = null
    private var movieTitleTV: TextView? = null
    private var auditoriumNameTV: TextView? = null
    private var dateTV: TextView? = null
    private var priceTV: TextView? = null
    private var timeSpinner: Spinner? = null

    private var auditorium: Auditorium? = null
    private var reservations: ArrayList<Reservation>? = null
    private var bookedSeat: ArrayList<Int>? = null
    private var selectedSeats: List<Int>? = null
    private var totalPrice: Int = 0
    private var times = listOf<String>()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_seat)

        val intent = intent
        cinema_name = intent.getStringExtra("cinema_name")
        movie_title = intent.getStringExtra("movie_title")
        date = intent.getStringExtra("date")
        seatPrice = intent.getIntExtra("price", 0)
        screenings = intent.getSerializableExtra("screenings") as ArrayList<Screening>
        screeningSelectedPos = intent.getIntExtra("screeningSelectedPos", 0)

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        times = screenings!!.map { dateFormat.format(it.screening_start) }

        seatBookView = findViewById(R.id.layoutSeat)
        cinemaNameTV = findViewById(R.id.activity_book_seat_cinema_name)
        movieTitleTV = findViewById(R.id.activity_book_seat_movie_name)
        auditoriumNameTV = findViewById(R.id.activity_book_seat_auditorium_name)
        dateTV = findViewById(R.id.activity_book_seat_date)
        priceTV = findViewById(R.id.activity_book_seat_price)

        cinemaNameTV!!.text = cinema_name
        movieTitleTV!!.text = movie_title
        dateTV!!.text = date

        buttonConfirm = findViewById(R.id.activity_book_seat_button_confirm)
        buttonConfirm?.setOnClickListener {
            val payIntent = Intent(applicationContext, PayActivity::class.java)
            intent.putStringArrayListExtra("selectedSeatsName", ArrayList(selectedSeatsName))
            intent.putIntegerArrayListExtra("selectedSeats", ArrayList(selectedSeats!!))
            intent.putExtra("totalPrice", totalPrice)
            startActivity(payIntent)
        }

        timeSpinner = findViewById(R.id.activity_book_seat_spinner_time)
        ArrayAdapter(
            this@BookSeatActivity,
            android.R.layout.simple_spinner_item,
            times
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            timeSpinner!!.adapter = adapter
        }
        timeSpinner!!.setSelection(screeningSelectedPos)
        timeSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                lifecycleScope.launch {
                    auditorium = getAuditorium(screenings!![p2].auditorium_id)
                    auditoriumNameTV!!.text = auditorium!!.name
                    reservations = getReservations(screenings!![p2].id)
                    bookedSeat = getBookedSeats(reservations!!)

                    runOnUiThread {
                        // Clear previous seat map and its data
                        resetSeatBookViewFields(seatBookView)
                        priceTV!!.text = "0đ"

                        seatBookView.setSeatsLayoutString(
                            makeExistedSeats(
                                auditorium!!.map,
                                bookedSeat!!
                            )
                        )
                            .isCustomTitle(true)
                            .setCustomTitle(makeExistedTitles(auditorium!!.map))
                            .setSeatSize(300)
                        seatBookView.show()
                        seatBookView.setSeatClickListener(object : SeatClickListener {
                            override fun onAvailableSeatClick(
                                selectedIdList: List<Int>,
                                view: View
                            ) {
                                val seat = view.findViewById<TextView>(view.id)
                                val seatName = seat.text.toString()

                                selectedSeatsName.add(seatName)
                                selectedSeats = selectedIdList.map { it - 1 }
                                totalPrice = selectedIdList.size * seatPrice!!
                                priceTV!!.text = toVND(totalPrice)

                                Log.i("Test", selectedSeatsName.toString())
                                Log.i("Test", selectedIdList.toString())
                            }

                            override fun onBookedSeatClick(view: View) {

                            }

                            override fun onReservedSeatClick(view: View) {
                            }
                        })
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun getBookedSeats(reservation: ArrayList<Reservation>): ArrayList<Int> {
        val res = arrayListOf<Int>()
        reservation.forEachIndexed { reservationIdx, reservationItem ->
            res.addAll(reservationItem.seats)
        }
        return res
    }

    private suspend fun getReservations(screening_id: String): ArrayList<Reservation>? =
        runCatching {
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

    fun resetSeatBookViewFields(seatBookView: SeatBookView) {
        selectedSeatsName.clear()
        (seatBookView.getChildAt(0) as LinearLayout).removeAllViews()

        val selectedIdListField = seatBookView::class.java.getDeclaredField("selectedIdList")
        selectedIdListField.isAccessible = true
        selectedIdListField.set(seatBookView, arrayListOf<Int>())

        val selectedSeatsField = seatBookView::class.java.getDeclaredField("selectedSeats")
        selectedSeatsField.isAccessible = true
        selectedSeatsField.set(seatBookView, 0)

        val countField = seatBookView::class.java.getDeclaredField("count")
        countField.isAccessible = true
        countField.set(seatBookView, 0)
    }
}