package com.example.admin.reviews

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.RequestCode
import com.example.admin.movies.Movie
import com.example.admin.users.User
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ReviewList : AppCompatActivity() {
    var movieRecyclerView: RecyclerView? = null

    var reviews: List<Review> = listOf()
    var movie: Movie? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        val intent = intent
        movie = intent.getSerializableExtra("movie") as? Movie

        movieRecyclerView = findViewById(R.id.reviewListRecyclerView)

        val adapter = ReviewListAdapter(this, reviews)
        movieRecyclerView!!.adapter = adapter
        movieRecyclerView!!.layoutManager = LinearLayoutManager(this)

        coroutineScope.launch {
            reviews = getReviewData()
            movieRecyclerView!!.adapter = ReviewListAdapter(this@ReviewList, reviews)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    suspend fun getReviewData(): List<Review> = runCatching {
        FirebaseApp.initializeApp(this@ReviewList)
        val db = Firebase.firestore
        val reviews = db.collection("review")
            .whereEqualTo("movie_id", movie!!.id)
            .get()
            .await()
            .toObjects(Review::class.java)

        val userIds = reviews.map { it.user_id }
        val users = db.collection("user")
            .whereIn(FieldPath.documentId(), userIds)
            .get()
            .await()
            .toObjects(User::class.java)
            .associateBy { it.id }

        reviews.forEach { it.user_name = users[it.user_id]?.username ?: "" }

        reviews
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        emptyList()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode.REVIEW_SCREEN_DETAIL) {
            if (resultCode == Activity.RESULT_OK) {
                recreate()
            }
        }
    }
}