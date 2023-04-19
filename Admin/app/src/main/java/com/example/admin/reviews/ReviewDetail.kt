package com.example.admin.reviews

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.admin.R
import com.example.admin.auditoriums.Auditorium
import com.example.admin.movies.Movie
import com.example.admin.users.User
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ReviewDetail : AppCompatActivity() {
    var movieNameET: EditText? = null
    var usernameET: EditText? = null
    var dateET: EditText? = null
    var ratingET: EditText? = null
    var descriptionET: EditText? = null
    var delBtn: Button? = null

    var review: Review? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_detail)
        FirebaseApp.initializeApp(this@ReviewDetail)

        val intent = intent
        review = intent.getSerializableExtra("review") as? Review

        movieNameET = findViewById(R.id.detailReviewMovieNameET)
        usernameET = findViewById(R.id.detailReviewUsernameET)
        dateET = findViewById(R.id.detailReviewDateET)
        ratingET = findViewById(R.id.detailReviewRatingET)
        descriptionET = findViewById(R.id.detailReviewDescriptionET)
        delBtn = findViewById(R.id.detailReviewDeleteBtn)

        dateET!!.setText(SimpleDateFormat("dd/MM/yyyy HH:mm",
            Locale.getDefault()).format(review!!.date))
        ratingET!!.setText(review!!.rating.toString())
        descriptionET!!.setText(review!!.detail)

        coroutineScope.launch {
            movieNameET!!.setText(getMovieData().title)
            usernameET!!.setText(getUserData().username)
        }

        delBtn!!.setOnClickListener {
            deleteReview()
        }

    }
    suspend fun getMovieData(): Movie = runCatching {
        val db = Firebase.firestore
        val result = db.collection("movie")
            .document(review!!.movie_id)
            .get()
            .await()
        result.toObject(Movie::class.java)?: Movie()
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        Movie()
    }
    suspend fun getUserData(): User = runCatching {
        val db = Firebase.firestore
        val result = db.collection("user")
            .document(review!!.user_id)
            .get()
            .await()
        result.toObject(User::class.java)?: User()
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        User()
    }
    fun deleteReview(): Boolean = runCatching {
        val db = Firebase.firestore
        db.collection("review")
            .document(review!!.id)
            .delete()
            .addOnSuccessListener {
                val replyIntent = Intent()
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        true
    }.getOrElse {
        Log.w("DB", "Error deleting document $review!!.id", it)
        false
    }

}