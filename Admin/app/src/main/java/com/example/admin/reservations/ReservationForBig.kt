package com.example.admin.reservations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.admin.R
import com.example.admin.auditoriums.Auditorium
import com.example.admin.seats.Seat
import com.example.admin.tickets.Ticket
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
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

    private var reservations: Map<String, CustomReservation>? = null
    private var bookedSeat: MutableSet<String>? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_for_big)
        FirebaseApp.initializeApp(this@ReservationForBig)

        coroutineScope.launch {
            auditorium = getAuditorium(auditorium_id)
            bookedSeat = getBookedSeats(screening_id)

            val titles = makeExistedTitles(auditorium!!.map)
            val seats = makeExistedSeats(auditorium!!.map, titles)
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
                    val reservation_id =
                        reservations!!.filterValues { it.seats.contains(seatName) }.keys.firstOrNull()
                    val reservation = reservations!!.getValue(reservation_id.toString())
                    val intent = Intent(this@ReservationForBig, ReservationForBigDetail::class.java)
                    intent.putExtra("reservation", reservation)
                    startActivity(intent)
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

    suspend fun getBookedSeats(screening_id: String): MutableSet<String>? = runCatching {
        val db = Firebase.firestore
        val bookedSeats: MutableSet<String> = mutableSetOf()
        val reservationArrayList: ArrayList<CustomReservation> = arrayListOf()

        // Query all reservations for the given screening_id
        val reservationsSnapshot = db.collection("reservation")
            .whereEqualTo("screening_id", screening_id)
            .get()
            .await()

        // Iterate over the reservations and get the tickets and seats for each one
        for (reservation in reservationsSnapshot) {
            val curReservation = reservation.toObject(CustomReservation::class.java)
            // Query the tickets for this reservation
            val ticketsSnapshot = db.collection("ticket")
                .whereEqualTo("reservation_id", reservation.id)
                .get()
                .await()

            val ticketList = ticketsSnapshot.toObjects(Ticket::class.java)

            // Iterate over the tickets
            for (ticket in ticketList) {
                // Query the seat for this ticket
                val seatSnapshot = db.collection("seat")
                    .document(ticket.seat_id)
                    .get()
                    .await()

                val seat = seatSnapshot.toObject(Seat::class.java)
                curReservation.seats.add(seat!!.row + seat.col)
                bookedSeats.add(seat.row + seat.col)
            }
            reservationArrayList.add(curReservation)
        }
        reservations = reservationArrayList.associateBy { it.id }

        bookedSeats
    }.getOrElse {
        Log.w("DB", "Error getting booked seats.", it)
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
        title: List<String>,
    ): String {
        val mapColNum = map[0].length
        var seats = ""
        // Seat map is like this ["111", "111", "111"]
        map.forEachIndexed { sIndex, s ->
            seats += "/"
            seats += s.mapIndexed { cIndex, c ->
                if (c == '1') {
                    // Titles is like this [/, A1, A2, A3, /, B1, B2, B3, /, C1, C2, C3]
                    // so increment col and row by 1 because of '/'
                    if (bookedSeat!!.contains(title[sIndex * (mapColNum + 1) + 1 + cIndex])) {
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