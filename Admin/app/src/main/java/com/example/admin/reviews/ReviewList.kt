package com.example.admin.reviews

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.movies.Movie
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat

class ReviewList : AppCompatActivity() {
    var tvTotalNumberRating: TextView? = null
    var totalStarRating: RatingBar? = null
    var tvTotalPemberiBintang: TextView? = null
    var llPercentage5: LinearLayout? = null
    var constrainLayout5: ConstraintLayout? = null
    var llPercentage4: LinearLayout? = null
    var constrainLayout4: ConstraintLayout? = null
    var llPercentage3: LinearLayout? = null
    var constrainLayout3: ConstraintLayout? = null
    var llPercentage2: LinearLayout? = null
    var constrainLayout2: ConstraintLayout? = null
    var llPercentage1: LinearLayout? = null
    var constrainLayout1: ConstraintLayout? = null
    var rvReview: RecyclerView? = null
    var movieName: TextView? = null

    private var movieReviewModel: MovieReviewModel? = null
    private var adapter: ReviewListAdapter? = null

    var movie: Movie? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        val intent = intent
        movie = intent.getSerializableExtra("movie") as Movie

        tvTotalNumberRating = findViewById(R.id.tv_total_number_rating)
        totalStarRating = findViewById(R.id.total_star_rating)
        tvTotalPemberiBintang = findViewById(R.id.tv_total_pemberi_bintang)
        llPercentage5 = findViewById(R.id.ll_percentage_5)
        constrainLayout5 = findViewById(R.id.constrain_layout_5)
        llPercentage4 = findViewById(R.id.ll_percentage_4)
        constrainLayout4 = findViewById(R.id.constrain_layout_4)
        llPercentage3 = findViewById(R.id.ll_percentage_3)
        constrainLayout3 = findViewById(R.id.constrain_layout_3)
        llPercentage2 = findViewById(R.id.ll_percentage_2)
        constrainLayout2 = findViewById(R.id.constrain_layout_2)
        llPercentage1 = findViewById(R.id.ll_percentage_1)
        constrainLayout1 = findViewById(R.id.constrain_layout_1)
        rvReview = findViewById(R.id.rv_review)
        movieName = findViewById(R.id.movie_name)
        movieName!!.text = movie!!.title

        coroutineScope.launch {
            initView()
        }
    }

    private suspend fun initView() {
        val db = Firebase.firestore
        val reviews = db.collection("review")
            .whereEqualTo("movie_id", movie!!.id)
            .get()
            .await()
            .toObjects(Review::class.java)

        val counts = mutableListOf<Int>()

        for (rating in 1..5) {
            val count = reviews.count { it.rating.toInt() == rating }
            counts.add(count)
        }

        movieReviewModel = MovieReviewModel(
            totalVoters = counts.sum(),
            totalRating = counts.withIndex().sumByDouble { (index, count) -> (index + 1) * count.toDouble() },
            star1 = counts.getOrElse(0) { 0 },
            star2 = counts.getOrElse(1) { 0 },
            star3 = counts.getOrElse(2) { 0 },
            star4 = counts.getOrElse(3) { 0 },
            star5 = counts.getOrElse(4) { 0 }
        )

        setRatingByColor(movieReviewModel!!)
        adapter = ReviewListAdapter(this@ReviewList, reviews)
        rvReview!!.adapter = adapter
        rvReview!!.layoutManager = LinearLayoutManager(this@ReviewList)
    }

    private fun setRatingByColor(productModel: MovieReviewModel) {
        val widthView = constrainLayout1!!.width
        val totalAllVoters = productModel.totalVoters
        val totalRateStar1 = productModel.star1
        val totalRateStar2 = productModel.star2
        val totalRateStar3 = productModel.star3
        val totalRateStar4 = productModel.star4
        val totalRateStar5 = productModel.star5

        //convert to double
        val votersInDouble = totalAllVoters.toDouble()

        //RATING STAR 1
        val star1 = totalRateStar1.toDouble()
        val sum1 = star1 / votersInDouble
        val rating1 = (sum1 * widthView).toInt()
        val layoutParams1 =
            ConstraintLayout.LayoutParams(rating1, ConstraintLayout.LayoutParams.MATCH_PARENT)
        layoutParams1.setMargins(0, 5, 0, 5)
        llPercentage1!!.setBackgroundColor(Color.parseColor("#ff6f31"))
        llPercentage1!!.layoutParams = layoutParams1

        //RATING STAR 2
        val star2 = totalRateStar2.toDouble()
        val sum2 = star2 / votersInDouble
        val rating2 = (sum2 * widthView).toInt()
        val layoutParams2 =
            ConstraintLayout.LayoutParams(rating2, ConstraintLayout.LayoutParams.MATCH_PARENT)
        layoutParams2.setMargins(0, 5, 0, 5)
        llPercentage2!!.setBackgroundColor(Color.parseColor("#ff9f02"))
        llPercentage2!!.layoutParams = layoutParams2

        //RATING STAR 3
        val star3 = totalRateStar3.toDouble()
        val sum3 = star3 / votersInDouble
        val rating3 = (sum3 * widthView).toInt()
        val layoutParams3 =
            ConstraintLayout.LayoutParams(rating3, ConstraintLayout.LayoutParams.MATCH_PARENT)
        layoutParams3.setMargins(0, 5, 0, 5)
        llPercentage3!!.setBackgroundColor(Color.parseColor("#ffcf02"))
        llPercentage3!!.layoutParams = layoutParams3

        //RATING STAR 4
        val star4 = totalRateStar4.toDouble()
        val sum4 = star4 / votersInDouble
        val rating4 = (sum4 * widthView).toInt()
        val layoutParams4 =
            ConstraintLayout.LayoutParams(rating4, ConstraintLayout.LayoutParams.MATCH_PARENT)
        layoutParams4.setMargins(0, 5, 0, 5)
        llPercentage4!!.setBackgroundColor(Color.parseColor("#9ace6a"))
        llPercentage4!!.layoutParams = layoutParams4

        //RATING STAR 5
        val star5 = totalRateStar5.toDouble()
        val sum5 = star5 / votersInDouble
        val rating5 = (sum5 * widthView).toInt()
        val layoutParams5 =
            ConstraintLayout.LayoutParams(rating5, ConstraintLayout.LayoutParams.MATCH_PARENT)
        layoutParams5.setMargins(0, 5, 0, 5)
        llPercentage5!!.setBackgroundColor(Color.parseColor("#57bb8a"))
        llPercentage5!!.layoutParams = layoutParams5

        var rating = 0.0
        if (votersInDouble != 0.0) {
            rating = productModel.totalRating.toDouble() / votersInDouble
        }
        val format = DecimalFormat(".#")
        val ratingText = if (rating == 0.0) "0" else format.format(rating)
        tvTotalNumberRating!!.text = ratingText
        totalStarRating!!.rating = (rating).toString().toFloat()
        tvTotalPemberiBintang!!.text = "$totalAllVoters total"
    }
}