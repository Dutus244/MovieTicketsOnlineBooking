package com.example.movieticketsonlinebooking.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.Movie
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class FilmInfoActivity : AppCompatActivity() {
    var adapter1: MyAdapter? = null
    var adapter2: MyAdapter? = null
    var castList: List<String> = listOf()
    var directorList: List<String> = listOf()

    class People(var name: String, var avatar: Int)

    val dateFormat = "dd/MM/yyyy"
    val dateFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())

    var movie: Movie? = null
    var moviePoster: ImageView? = null
    var movieTitle: TextView? = null
    var movieRating: TextView? = null
    var movieClassification: TextView? = null
    var movieDuration: TextView? = null
    var movieDescription: TextView? = null
    var movieReleaseDate: TextView? = null
    var showtimesButton: Button? = null
    var showReviewButton: Button? = null

    class MyAdapter(
        private val context: Context,
        private val list: List<String>
    ) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewName: TextView = itemView.findViewById(R.id.textViewName)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_layout_listview_list_actors, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewName.text = list[position]
            holder.imageView.setImageResource(R.drawable.test_avatar_2)
            holder.itemView.setOnClickListener {

            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_info)

        intent = intent
        movie = intent.getSerializableExtra("movie") as Movie


        moviePoster = findViewById(R.id.activity_film_info_imageview_avatar)
        movieTitle = findViewById(R.id.activity_film_info_textview_name)
        movieRating = findViewById(R.id.activity_film_info_textview_rating)
        movieClassification = findViewById(R.id.activity_film_info_textview_age)
        movieDuration = findViewById(R.id.activity_film_info_textview_time)
        movieReleaseDate = findViewById(R.id.activity_film_info_textview_date)
        movieDescription = findViewById(R.id.my_textview)
        castList = movie!!.cast.split(",")
        directorList = movie!!.director.split(",")

        Picasso.get().load(movie!!.poster_url).into(moviePoster)
        movieTitle!!.text = movie!!.title
        movieRating!!.text = movie!!.rating.toString()
        movieClassification!!.text = movie!!.classification
        when (movie!!.classification) {
            "P" -> {
                movieClassification!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.movieClassification_P
                    )
                )
            }
            "C13" -> {
                movieClassification!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.movieClassification_C13
                    )
                )
            }
            "C16" -> {
                movieClassification!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.movieClassification_C16
                    )
                )
            }
            "C18" -> {
                movieClassification!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.movieClassification_C18
                    )
                )
            }
        }
        movieDuration!!.text = getString(R.string.movie_duration, movie!!.duration)
        movieReleaseDate!!.text = dateFormatter.format(movie!!.release_date)
        movieDescription!!.text = movie!!.description

        val youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youtube_player_view)
        youTubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                // change the video ID to the new one
                val newVideoId = extractYouTubeVideoId(movie!!.vid_url)
                youTubePlayer.cueVideo(newVideoId, 0f)
            }
        })

        val seeMoreButton: Button = findViewById(R.id.see_more_button)
        val seeLessButton: Button = findViewById(R.id.see_less_button)
        seeMoreButton.setOnClickListener {
            movieDescription!!.maxLines = Int.MAX_VALUE
            movieDescription!!.ellipsize = null
            seeMoreButton.visibility = View.GONE
            seeLessButton.visibility = View.VISIBLE
        }
        seeLessButton.setOnClickListener {
            movieDescription!!.maxLines = 3
            movieDescription!!.ellipsize = TextUtils.TruncateAt.END
            seeLessButton.visibility = View.GONE
            seeMoreButton.visibility = View.VISIBLE
        }

        val recyclerView1: RecyclerView = findViewById(R.id.customRecyclerView1)
        val layoutManager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.layoutManager = layoutManager1
        adapter1 = MyAdapter(this, castList)
        recyclerView1.adapter = adapter1

        val recyclerView2: RecyclerView = findViewById(R.id.customRecyclerView2)
        val layoutManager2 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.layoutManager = layoutManager2
        adapter2 = MyAdapter(this, directorList)
        recyclerView2.adapter = adapter2

        showtimesButton = findViewById(R.id.activity_film_info_showtimes)
        showtimesButton?.setOnClickListener {
            val intent = Intent(applicationContext, FilmShowtimesActivity::class.java)
            intent.putExtra("movie_id", movie!!.id)
            startActivity(intent)
        }
    }

    fun extractYouTubeVideoId(youtubeUrl: String): String {
        // Regular expression pattern to match YouTube video URLs
        val pattern = "(?<=v=|\\/videos\\/|\\/watch\\?v=|\\/embed\\/)[^#\\&\\?\\n]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(youtubeUrl)

        if (matcher.find()) {
            // Extract the YouTube video ID from the matched pattern
            return matcher.group()
        }

        // If no match is found, return null
        return ""
    }
}