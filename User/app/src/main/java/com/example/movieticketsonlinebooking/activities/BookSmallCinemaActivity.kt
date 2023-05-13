package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.Auditorium
import com.example.movieticketsonlinebooking.viewmodels.Cinema
import com.example.movieticketsonlinebooking.viewmodels.Screening
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import dev.jahidhasanco.seatbookview.SeatClickListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookSmallCinemaActivity : AppCompatActivity() {
    var cinema: Cinema? = null
    private var cinema_name: String? = null
    private var movie_title: String? = null
    private var date: String? = null
    private var screenings: ArrayList<Screening>? = null
    private var screeningSelectedPos: Int = 0
    private var times = listOf<String>()

    private var cinemaNameTV: TextView? = null
    private var movieTitleTV: TextView? = null
    private var auditoriumNameTV: TextView? = null
    private var dateTV: TextView? = null
    private var timeSpinner: Spinner? = null

    private var auditorium: Auditorium? = null

    var imageView: ImageView? = null
    var textViewDisc: TextView? = null
    var textViewPrice: TextView? = null
    var textViewStatus: TextView? = null
    var buttonNext: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_small_cinema)

        intent = intent
        cinema_name = intent.getStringExtra("cinema_name")
        movie_title = intent.getStringExtra("movie_title")
        date = intent.getStringExtra("date")
        screenings = intent.getSerializableExtra("screenings") as ArrayList<Screening>
        screeningSelectedPos = intent.getIntExtra("screeningSelectedPos", 0)

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        times = screenings!!.map { dateFormat.format(it.screening_start) }

        cinemaNameTV = findViewById(R.id.activity_book_small_film_name)
        movieTitleTV = findViewById(R.id.activity_book_seat_movie_name)
        auditoriumNameTV = findViewById(R.id.activity_book_seat_auditorium_name)
        dateTV = findViewById(R.id.activity_book_seat_date)

        imageView = findViewById(R.id.image)
        textViewDisc = findViewById(R.id.textview_discription)
        textViewPrice = findViewById(R.id.textview_price)
        textViewStatus = findViewById(R.id.textview_status)

        cinemaNameTV!!.text = cinema_name
        movieTitleTV!!.text = movie_title
        dateTV!!.text = date

        timeSpinner = findViewById(R.id.activity_book_seat_spinner_time)
        ArrayAdapter(
            this@BookSmallCinemaActivity,
            android.R.layout.simple_spinner_item,
            times
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            timeSpinner!!.adapter = adapter
        }
        var status: Boolean = true
        timeSpinner!!.setSelection(screeningSelectedPos)
        timeSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                lifecycleScope.launch {
                    auditorium = getAuditorium(screenings!![p2].auditorium_id)
                    auditoriumNameTV!!.text = auditorium!!.name

                    Picasso.get().load(auditorium!!.img_url).into(imageView)

                    textViewDisc!!.text = auditorium!!.description
                    textViewPrice!!.text = toVND(auditorium!!.price)

                    val db = Firebase.firestore
                    val collectionRef = db.collection("reservation")
                    val query = collectionRef.whereEqualTo("screening_id", screenings!![p2].id)

                    query.get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val querySnapshot = task.result
                                if (querySnapshot != null && !querySnapshot.isEmpty) {
                                    textViewStatus!!.text = "Đã được đặt"
                                    status = false
                                } else {
                                    // No documents found
                                    textViewStatus!!.text = "Trống"
                                    status = true
                                }
                            } else {
                                // An error occurred
                                val exception = task.exception
                                // Handle the error
                            }
                        }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        var arraySeatName : ArrayList<String> = ArrayList()
        var arraySelectedSeat : ArrayList<Int> = ArrayList()

        buttonNext = findViewById(R.id.activity_book_seat_button_confirm)
        buttonNext!!.setOnClickListener {
            if (status) {
                val payIntent = Intent(this, PayActivity::class.java)
                payIntent.putExtra("screening", screenings!![screeningSelectedPos])
                payIntent.putExtra("auditorium_name", auditorium!!.name)
                payIntent.putExtra("cinema_name", cinema_name)
                payIntent.putStringArrayListExtra("selectedSeatsName", arraySeatName)
                payIntent.putIntegerArrayListExtra("selectedSeats", arraySelectedSeat)
                payIntent.putExtra("totalPrice", auditorium!!.price)
                startActivity(payIntent)
            }
            else {
                Toast.makeText(applicationContext, "Phòng đã có người đặt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }

    private fun toVND(num: Int): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return formatter.format(num) + "đ"
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
}