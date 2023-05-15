package com.example.admin.reservations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.admin.R
import com.example.admin.auditoriums.Auditorium
import com.example.admin.screenings.Screening
import com.example.admin.users.User
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ReservationForSmall : AppCompatActivity() {
    private var userTV: TextView? = null
    private var dateTV: TextView? = null
    private var movieNameTV: TextView? = null
    private var audiNameTV: TextView? = null
    private var totalPriceTV: TextView? = null

    // Get from previous activity
    private var movie_title: String = ""
    private var auditorium_id: String = ""
    private var screening_id: String = ""

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_for_small)
        FirebaseApp.initializeApp(this@ReservationForSmall)

        val intent = intent
        val screening = intent.getSerializableExtra("screening") as Screening
        movie_title = intent.getStringExtra("movie_title")!!
        auditorium_id = screening.auditorium_id
        screening_id = screening.id

        userTV = findViewById(R.id.userTV)
        dateTV = findViewById(R.id.dateTV)
        movieNameTV = findViewById(R.id.reservationSmallMovieNameTV)
        audiNameTV = findViewById(R.id.curAudiTV)
        totalPriceTV = findViewById(R.id.totalPriceTV)

        coroutineScope.launch {
            val reservation = getReservation(screening_id)
            if (reservation == null) {
                userTV!!.append("Chưa")
                return@launch
            }
            val auditorium = getAuditorium(auditorium_id)
            val user = getUser(reservation.user_id)



            userTV!!.append(user!!.username)
            dateTV!!.append(
                SimpleDateFormat(
                    "dd/MM/yyyy hh:mm:ss",
                    Locale.getDefault()
                ).format(reservation.date)
            )
            movieNameTV!!.append(movie_title)
            audiNameTV!!.append(auditorium!!.name)
            totalPriceTV!!.append(reservation.total_price.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    suspend fun getUser(user_id: String): User? = runCatching {
        val db = Firebase.firestore
        val res = db.collection("user")
            .document(user_id)
            .get()
            .await()
        res.toObject(User::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting data .", it)
        null
    }

    suspend fun getAuditorium(auditorium_id: String): Auditorium? = runCatching {
        val db = Firebase.firestore
        val res = db.collection("auditorium")
            .document(auditorium_id)
            .get()
            .await()
        res.toObject(Auditorium::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting data .", it)
        null
    }

    suspend fun getReservation(screening_id: String): Reservation? = runCatching {
        val db = Firebase.firestore
        val res = db.collection("reservation")
            .whereEqualTo("screening_id", screening_id)
            .limit(1)
            .get()
            .await()
        res.toObjects(Reservation::class.java)[0]
    }.getOrElse {
        Log.w("DB", "Error getting data .", it)
        null
    }
}