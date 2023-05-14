package com.example.movieticketsonlinebooking.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.Movie
import com.example.movieticketsonlinebooking.viewmodels.Reservation
import com.example.movieticketsonlinebooking.viewmodels.Screening
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class PaymentHistoryDetail : AppCompatActivity() {
    private var moviePosterIV: ImageView? = null
    private var movieTitleTV: TextView? = null
    private var movieClassificationTV: TextView? = null
    private var movieDurationTV: TextView? = null
    private var cinemaNameTV: TextView? = null
    private var screeningTimeTV: TextView? = null
    private var screeningDateTV: TextView? = null
    private var seatTitleTV: TextView? = null
    private var seatsTV: TextView? = null
    private var reservationIdTV: TextView? = null
    private var totalPriceTV: TextView? = null
    private var closeBtn: Button? = null

    private var callingActivity: String? = null
    private var screening: Screening? = null
    private var auditorium_name: String? = null
    private var cinema_name: String? = null
    private var reservation_id: String? = null

    private var reservation: Reservation? = null
    private var movie: Movie? = null
    lateinit var qrIV: ImageView
    lateinit var bitmap: Bitmap
    lateinit var qrEncoder: QRGEncoder
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_history_detail)

        moviePosterIV = findViewById(R.id.activity_payment_history_detail_film_avatar)
        movieTitleTV = findViewById(R.id.activity_payment_history_detail_film_name)
        movieClassificationTV = findViewById(R.id.activity_payment_history_detail_film_age)
        movieDurationTV = findViewById(R.id.activity_payment_history_detail_film_time)
        cinemaNameTV = findViewById(R.id.activity_payment_history_detail_cinema_name)
        screeningTimeTV = findViewById(R.id.activity_payment_history_detail_film_time_start)
        screeningDateTV = findViewById(R.id.activity_payment_history_detail_film_time_date)
        seatTitleTV = findViewById(R.id.activity_payment_history_detail_seat_title)
        seatsTV = findViewById(R.id.activity_payment_history_detail_seats)
        reservationIdTV = findViewById(R.id.activity_payment_history_detail_reservation_id)
        totalPriceTV = findViewById(R.id.activity_payment_history_detail_total_price)
        closeBtn = findViewById(R.id.ctivity_payment_history_detail_closeBtn)

        val intent = intent
        callingActivity = intent.getStringExtra("caller")
        screening = intent.getSerializableExtra("screening") as Screening
        auditorium_name = intent.getStringExtra("auditorium_name")
        cinema_name = intent.getStringExtra("cinema_name")
        reservation_id = intent.getStringExtra("reservation_id")
        qrIV = findViewById(R.id.idIVQrcode)

        closeBtn!!.setOnClickListener {
            if (callingActivity == "PayActivity") {
                val homeIntent = Intent(this@PaymentHistoryDetail, HomeActivity::class.java)
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(homeIntent)
            }
            finish()
        }
        // set up back button listener
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // handle back button press here
                if (callingActivity == "PayActivity") {
                    val homeIntent = Intent(this@PaymentHistoryDetail, HomeActivity::class.java)
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(homeIntent)
                }
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        lifecycleScope.launch {
            reservation = getReservation(reservation_id!!)
            movie = getMovie(screening!!.movie_id)

            Picasso.get().load(movie!!.poster_url).into(moviePosterIV)
            movieTitleTV!!.text = movie!!.title
            movieClassificationTV!!.text = movie!!.classification
            when (movie!!.classification) {
                "P" -> {
                    movieClassificationTV!!.setBackgroundColor(
                        ContextCompat.getColor(
                            this@PaymentHistoryDetail,
                            R.color.movieClassification_P
                        )
                    )
                }
                "C13" -> {
                    movieClassificationTV!!.setBackgroundColor(
                        ContextCompat.getColor(
                            this@PaymentHistoryDetail,
                            R.color.movieClassification_C13
                        )
                    )
                }
                "C16" -> {
                    movieClassificationTV!!.setBackgroundColor(
                        ContextCompat.getColor(
                            this@PaymentHistoryDetail,
                            R.color.movieClassification_C16
                        )
                    )
                }
                "C18" -> {
                    movieClassificationTV!!.setBackgroundColor(
                        ContextCompat.getColor(
                            this@PaymentHistoryDetail,
                            R.color.movieClassification_C18
                        )
                    )
                }
            }
            movieDurationTV!!.text = movie!!.duration.toString() + " phút"
            cinemaNameTV!!.text = "$cinema_name - $auditorium_name"
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            screeningTimeTV!!.text = timeFormat.format(screening!!.screening_start)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            screeningDateTV!!.text = dateFormat.format(screening!!.screening_start)
            if (reservation!!.seats.isEmpty()) {
                seatTitleTV!!.visibility = View.GONE
                seatsTV!!.visibility = View.GONE
            } else {
                seatsTV!!.text = reservation!!.seats_name.joinToString(", ")
            }
            reservationIdTV!!.text = reservation_id
            totalPriceTV!!.text = toVND(reservation!!.total_price)

            val windowManager: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            val display: Display = windowManager.defaultDisplay

            val point: Point = Point()
            display.getSize(point)

            val width = point.x
            val height = point.y

            var dimen = if (width < height) width else height
            dimen = dimen * 3 / 4

            // on below line we are initializing our qr encoder
            qrEncoder = QRGEncoder(reservation_id, null, QRGContents.Type.TEXT, dimen)
            qrEncoder.setColorBlack(Color.WHITE);
            qrEncoder.setColorWhite(Color.BLACK);

            // on below line we are running a try
            // and catch block for initializing our bitmap
            try {
                // on below line we are
                // initializing our bitmap
                bitmap = qrEncoder.getBitmap();

                // on below line we are setting
                // this bitmap to our image view
                qrIV.setImageBitmap(bitmap)
            } catch (e: Exception) {
                // on below line we
                // are handling exception
                e.printStackTrace()
            }
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

    suspend fun getReservation(reservation_id: String): Reservation? = runCatching {
        val db = Firebase.firestore
        val result = db.collection("reservation")
            .document(reservation_id)
            .get()
            .await()
        result.toObject(Reservation::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        null
    }

    private fun toVND(num: Int): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return formatter.format(num) + "đ"
    }
}