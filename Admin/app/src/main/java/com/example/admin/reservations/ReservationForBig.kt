package com.example.admin.reservations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.admin.R
import com.example.admin.auditoriums.Auditorium
import com.example.admin.users.User
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dev.jahidhasanco.seatbookview.SeatBookView
import dev.jahidhasanco.seatbookview.SeatClickListener
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ReservationForBig : AppCompatActivity() {
    private lateinit var seatBookView: SeatBookView
    private var auditorium: Auditorium? = null

    // Get from previous activity
    private var auditorium_id: String = "s3zfbcJCAKCsmADYz9KU"
    private var screening_id: String = "8Fi0mDdLOCoBAA5TLXxj"

    private var reservations: ArrayList<Reservation>? = null
    private var bookedSeat: ArrayList<Int>? = null
    private var seatToReservation: HashMap<Int, Int> = hashMapOf()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_for_big)
        FirebaseApp.initializeApp(this@ReservationForBig)

        coroutineScope.launch {
            reservations = getReservations(screening_id)
            auditorium = getAuditorium(auditorium_id)
            bookedSeat = getBookedSeats(reservations!!)


            val titles = makeExistedTitles(auditorium!!.map)
            val seats = makeExistedSeats(auditorium!!.map, bookedSeat!!)
            seatBookView = findViewById(R.id.layoutSeat)
            seatBookView.setSeatsLayoutString(seats)
                .isCustomTitle(true)
                .setCustomTitle(titles)
                .setSeatLayoutPadding(2)
                .setSeatSize(400)
                .setSelectSeatLimit(0)
            seatBookView.show()
            seatBookView.setSeatClickListener(object : SeatClickListener {
                override fun onAvailableSeatClick(selectedIdList: List<Int>, view: View) {
                }

                override fun onBookedSeatClick(view: View) {
                    val seat = view.findViewById<TextView>(view.id)
                    val seatName = seat.text.toString()
                    val pureTitles = titles.filter { it != "/" }
                    val idx = pureTitles.indexOf(seatName)
                    val re = reservations!![seatToReservation[idx]!!]
                    val db = Firebase.firestore
                    db.collection("user")
                        .document(re.user_id)
                        .get()
                        .addOnSuccessListener {
                            val user = it.toObject(User::class.java)
                            val intent =
                                Intent(this@ReservationForBig, ReservationForBigDetail::class.java)
                            intent.putExtra("user", user!!.username)
                            intent.putExtra("reservation", re)
                            intent.putExtra("curSeat", seatName)
                            intent.putStringArrayListExtra("titles", ArrayList(pureTitles))
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Log.w("DB", "Error getting data", e)
                        }
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

    fun getBookedSeats(reservation: ArrayList<Reservation>): ArrayList<Int> {
        val res = arrayListOf<Int>()
        reservation.forEachIndexed { reservationIdx, reservationItem ->
            res.addAll(reservationItem.seats)
            reservationItem.seats.forEachIndexed { _, seatItem ->
                seatToReservation[seatItem] = reservationIdx
            }
        }
        return res
    }

    suspend fun getReservations(screening_id: String): ArrayList<Reservation>? = runCatching {
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

    suspend fun getAuditorium(auditorium_id: String): Auditorium? = runCatching {
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

    fun makeExistedSeats(
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

    fun makeExistedTitles(map: ArrayList<String>): List<String> {
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
}