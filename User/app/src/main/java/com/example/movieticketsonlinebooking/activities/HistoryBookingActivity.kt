package com.example.movieticketsonlinebooking.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.Screening
import com.example.movieticketsonlinebooking.viewmodels.UserManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class HistoryBookingActivity : AppCompatActivity() {
    var reservations: List<Reservation> = listOf()

    var historyRecyclerView: RecyclerView? = null
    var adapter = HistoryListAdapter(this, reservations)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_booking)
        FirebaseApp.initializeApp(this@HistoryBookingActivity)
        historyRecyclerView = findViewById(R.id.historyListRecyclerView)

        historyRecyclerView!!.adapter = adapter
        historyRecyclerView!!.layoutManager = LinearLayoutManager(this)

        getHistoryData()
    }
    fun getHistoryData() {
        val db = Firebase.firestore
        val reservationsCollection = db.collection("reservation")
        val user = UserManager.getCurrentUser()
        if (user != null) {
            val userId = user.userID
            reservationsCollection
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    reservations = listOf()
                    val screeningCollection = db.collection("screening")
                    val movieCollection = db.collection("movie")
                    val auditoriumCollection = db.collection("auditorium")
                    val cinemaCollection = db.collection("cinema")
                    val reservationCount = querySnapshot.size()
                    var retrievedCount = 0
                    for (document in querySnapshot) {
                        val reservation = document.toObject(Reservation::class.java)

                        val screeningId = reservation.screening_id
                        screeningCollection.document(screeningId)
                            .get()
                            .addOnSuccessListener { screeningDocument ->
                                reservation.screening = screeningDocument.toObject(Screening::class.java)!!
                                val movieId = screeningDocument.getString("movie_id")
                                movieCollection.document(movieId!!)
                                    .get()
                                    .addOnSuccessListener { movieDocument ->
                                        val movieName = movieDocument.getString("title")
                                        val moviePoster = movieDocument.getString("poster_url")
                                        reservation.movie_name = movieName ?: ""
                                        reservation.poster_url = moviePoster ?: ""
                                        val auditoriumId = screeningDocument.getString("auditorium_id")
                                        auditoriumCollection.document(auditoriumId!!)
                                            .get()
                                            .addOnSuccessListener { auditoriumDocument ->
                                                val auditoriumName = auditoriumDocument.getString("name")
                                                val cinemaId = auditoriumDocument.getString("cinema_id")

                                                reservation.auditorium_name = auditoriumName ?: ""

                                                cinemaCollection.document(cinemaId!!)
                                                    .get()
                                                    .addOnSuccessListener { cinemaDocument ->
                                                        val cinemaName = cinemaDocument.getString("name")

                                                        reservation.cinema_name = cinemaName ?: ""

                                                        reservations = reservations.plus(reservation)
                                                        retrievedCount++

                                                        if (retrievedCount == reservationCount) {
                                                            reservations = reservations.sortedWith(compareByDescending { it.date })
                                                            adapter.updateData(reservations)
                                                        }
                                                    }
                                                    .addOnFailureListener { exception ->
                                                        retrievedCount++

                                                        if (retrievedCount == reservationCount) {
                                                            Log.e("Error", "Cinema: " + exception)
                                                        }
                                                    }
                                            }
                                            .addOnFailureListener { exception ->
                                                retrievedCount++

                                                if (retrievedCount == reservationCount) {
                                                    Log.e("Error", "Movie: " + exception)
                                                }
                                            }
                                    }
                                    .addOnFailureListener { exception ->
                                        retrievedCount++

                                        if (retrievedCount == reservationCount) {
                                            Log.e("Error", "Screening: " + exception)
                                        }
                                    }
                            }
                            .addOnFailureListener { exception ->
                                retrievedCount++

                                if (retrievedCount == reservationCount) {
                                    Log.e("Error", "Reservation: " + exception)
                                }
                            }
                    }
                }
                .addOnFailureListener { exception ->
                }
        }
    }
}

@IgnoreExtraProperties
class Reservation(
    var auditorium_id: String = "",
    var screening_id: String = "",
    var user_id: String = "",
    var total_price: Int = 0,
    var date: Date = Date(),
    var seats: ArrayList<Int> = arrayListOf(),
    var seats_name: ArrayList<String> = arrayListOf(),
    @DocumentId
    val id: String = "",
    var movie_name: String = "",
    var poster_url: String = "",
    var auditorium_name: String = "",
    var cinema_name: String = "",
    var screening: Screening = Screening()
) : java.io.Serializable {
    override fun toString(): String {
        return "Reservation(id=$id, auditorium_id=$auditorium_id, screening_id=$screening_id, user_id=$user_id, total_price=$total_price, date=$date, seats=$seats, seats_name=$seats_name, movieName=$movie_name, auditoriumName=$auditorium_name, cinemaName=$cinema_name)"
    }
}

class HistoryListAdapter(private val activity: Activity, private var list: List<Reservation>) :
    RecyclerView.Adapter<HistoryListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        var position: Int? = null
        val imageView: ImageView = listItemView.findViewById(R.id.movieImg) as ImageView
        val nameText = listItemView.findViewById(R.id.movieNameTV) as TextView
        val locationText = listItemView.findViewById(R.id.movieLocationTV) as TextView
        val timeText = listItemView.findViewById(R.id.movieScreeningTimeTV) as TextView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val movieView = inflater.inflate(R.layout.history_movie_item, parent, false)
        return ViewHolder(movieView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picasso = Picasso.get()
        holder.position = position
        picasso.load(list[position].poster_url).into(holder.imageView)
        holder.nameText.text = list[position].movie_name
        holder.locationText.text = list[position].cinema_name + " - " + list[position].auditorium_name
        holder.timeText.text = SimpleDateFormat("HH:mm, dd/MM/yyyy",
            Locale.getDefault()).format(list[position].date)

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, PaymentHistoryDetail::class.java)
            intent.putExtra("caller", "HistoryBookingActivity")
            intent.putExtra("screening", list[position].screening)
            intent.putExtra("auditorium_name", list[position].auditorium_name)
            intent.putExtra("cinema_name", list[position].cinema_name)
            intent.putExtra("reservation_id", list[position].id)
            activity.startActivityForResult(intent, 0)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(newList: List<Reservation>) {
        list = newList
        notifyDataSetChanged()
    }
}