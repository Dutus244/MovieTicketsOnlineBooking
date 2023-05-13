package com.example.movieticketsonlinebooking.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.Movie
import com.example.movieticketsonlinebooking.viewmodels.Reservation
import com.example.movieticketsonlinebooking.viewmodels.Screening
import com.example.movieticketsonlinebooking.viewmodels.UserManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PayActivity : AppCompatActivity() {
    private var moviePosterIV: ImageView? = null
    private var movieTitleTV: TextView? = null
    private var movieClassificationTV: TextView? = null
    private var movieDurationTV: TextView? = null
    private var cinemaNameTV: TextView? = null
    private var screeningTimeTV: TextView? = null
    private var screeningDateTV: TextView? = null
    private var seatsTV: TextView? = null
    private var seatPriceTV: TextView? = null
    private var totalPriceTV: TextView? = null
    private var totalPriceConfirmTV: TextView? = null
    private var payRG: RadioGroup? = null
    private var payBtn: Button? = null

    private var screening: Screening? = null
    private var auditorium_name: String? = null
    private var cinema_name: String? = null
    private var selectedSeatsName: ArrayList<String>? = null
    private var selectedSeats: ArrayList<Int>? = null
    private var totalPrice: Int = 0

    private var movie: Movie? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        moviePosterIV = findViewById(R.id.activity_pay_film_avatar)
        movieTitleTV = findViewById(R.id.activity_pay_film_name)
        movieClassificationTV = findViewById(R.id.activity_pay_film_age)
        movieDurationTV = findViewById(R.id.activity_pay_film_time)
        cinemaNameTV = findViewById(R.id.activity_pay_cinema_name)
        screeningTimeTV = findViewById(R.id.activity_pay_film_time_start)
        screeningDateTV = findViewById(R.id.activity_pay_film_time_date)
        seatsTV = findViewById(R.id.activity_pay_seat_info)
        seatPriceTV = findViewById(R.id.activity_pay_seat_price)
        totalPriceTV = findViewById(R.id.activity_pay_seat_price_total)
        totalPriceConfirmTV = findViewById(R.id.activity_pay_seat_price_total_confirm)
        payRG = findViewById(R.id.activity_pay_radio_group)
        payBtn = findViewById(R.id.activity_pay_payBtn)

        val intent = intent
        screening = intent.getSerializableExtra("screening") as Screening
        auditorium_name = intent.getStringExtra("auditorium_name")
        cinema_name = intent.getStringExtra("cinema_name")
        selectedSeatsName = intent.getStringArrayListExtra("selectedSeatsName")
        selectedSeats = intent.getIntegerArrayListExtra("selectedSeats")
        totalPrice = intent.getIntExtra("totalPrice", 0)

        selectedSeatsName!!.sort()

        payBtn!!.setOnClickListener {
            if (payRG!!.checkedRadioButtonId == -1) {
                Toast.makeText(
                    this@PayActivity,
                    "Vui lòng chọn phương thức thanh toán",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }
            val reservation = Reservation(
                screening!!.auditorium_id,
                screening!!.id,
                UserManager.getCurrentUser().userID!!,
                totalPrice,
                Date(),
                selectedSeats!!,
                selectedSeatsName!!,
            )
            addReservation(reservation)
        }


        lifecycleScope.launch {
            movie = getMovie(screening!!.movie_id)

            Picasso.get().load(movie!!.poster_url).into(moviePosterIV)
            movieTitleTV!!.text = movie!!.title
            movieClassificationTV!!.text = movie!!.classification
            when (movie!!.classification) {
                "P" -> {
                    movieClassificationTV!!.setBackgroundColor(
                        ContextCompat.getColor(
                            this@PayActivity,
                            R.color.movieClassification_P
                        )
                    )
                }
                "C13" -> {
                    movieClassificationTV!!.setBackgroundColor(
                        ContextCompat.getColor(
                            this@PayActivity,
                            R.color.movieClassification_C13
                        )
                    )
                }
                "C16" -> {
                    movieClassificationTV!!.setBackgroundColor(
                        ContextCompat.getColor(
                            this@PayActivity,
                            R.color.movieClassification_C16
                        )
                    )
                }
                "C18" -> {
                    movieClassificationTV!!.setBackgroundColor(
                        ContextCompat.getColor(
                            this@PayActivity,
                            R.color.movieClassification_C18
                        )
                    )
                }
            }
            movieDurationTV!!.text = movie!!.duration.toString()
            cinemaNameTV!!.text = "$cinema_name - $auditorium_name"
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            screeningTimeTV!!.text = timeFormat.format(screening!!.screening_start)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            screeningDateTV!!.text = dateFormat.format(screening!!.screening_start)
            seatsTV!!.text = selectedSeatsName!!.joinToString(", ")
            if (selectedSeats!!.size != 0) {
                seatPriceTV!!.text = toVND(totalPrice / selectedSeats!!.size)
            }
            else {
                seatPriceTV!!.text = toVND(totalPrice)
            }
            totalPriceTV!!.text = toVND(totalPrice)
            totalPriceConfirmTV!!.text = toVND(totalPrice)
        }
    }

    suspend fun getMovie(movie_id: String): Movie? = runCatching {
        val db = Firebase.firestore
        val result = db.collection("movie")
            .document(movie_id)
            .get()
            .await()
        result.toObject(Movie::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        null
    }

    fun addReservation(reservation: Reservation) {
        val db = Firebase.firestore
        db.collection("reservation")
            .add(reservation)
            .addOnSuccessListener { it ->
                payBtn!!.isEnabled = false
                payBtn!!.isClickable = false
                Toast.makeText(
                    this@PayActivity,
                    "Thanh toán thành công",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this, PaymentHistoryDetail::class.java)
                intent.putExtra("caller", "PayActivity")
                intent.putExtra("screening", screening)
                intent.putExtra("auditorium_name", auditorium_name)
                intent.putExtra("cinema_name", cinema_name)
                intent.putExtra("reservation_id", it.id)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error adding document", e)
            }
    }

    private fun toVND(num: Int): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return formatter.format(num) + "đ"
    }
}