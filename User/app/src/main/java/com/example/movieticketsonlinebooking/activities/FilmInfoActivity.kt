package com.example.movieticketsonlinebooking.activities

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieticketsonlinebooking.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class FilmInfoActivity : AppCompatActivity() {
    var adapter1: MyAdapter? = null
    var adapter2: MyAdapter? = null
    var actorList: ArrayList<People> = ArrayList()
    var directorList: ArrayList<People> = ArrayList()
    class People(var name: String, var avatar: Int )

    class MyAdapter(private val context: Context, private val arrayList: java.util.ArrayList<People>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewName: TextView = itemView.findViewById(R.id.textViewName)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_layout_listview_list_actors, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = arrayList[position]
            holder.textViewName.text = data.name
            holder.imageView.setImageResource(data.avatar)
            holder.itemView.setOnClickListener {

            }
        }

        override fun getItemCount(): Int {
            return arrayList.size
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_info)


        val youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youtube_player_view)
        youTubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                // change the video ID to the new one
                val newVideoId = "BrUWjMW2vYQ"
                youTubePlayer.cueVideo(newVideoId, 0f)
            }
        })

        val seeMoreButton: Button = findViewById(R.id.see_more_button)
        val seeLessButton: Button = findViewById(R.id.see_less_button)
        val myTextView = findViewById<TextView>(R.id.my_textview)
        seeMoreButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                myTextView.maxLines = Int.MAX_VALUE
                myTextView.ellipsize = null
                seeMoreButton.setVisibility(View.GONE)
                seeLessButton.setVisibility(View.VISIBLE)
            }
        })
        seeLessButton.setOnClickListener {
            myTextView.maxLines = 3
            myTextView.ellipsize = TextUtils.TruncateAt.END
            seeLessButton.visibility = View.GONE
            seeMoreButton.visibility = View.VISIBLE
        }

        actorList.add(People("Skipper",R.drawable.test_avatar))
        actorList.add(People("Kowalski",R.drawable.test_avatar))
        actorList.add(People("Private",R.drawable.test_avatar))
        actorList.add(People("Rico",R.drawable.test_avatar))

        directorList.add(People("Skipper",R.drawable.test_avatar))

        val recyclerView1: RecyclerView = findViewById(R.id.customRecyclerView1)
        val layoutManager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.layoutManager = layoutManager1
        adapter1 = MyAdapter(this, actorList)
        recyclerView1.adapter = adapter1

        val recyclerView2: RecyclerView = findViewById(R.id.customRecyclerView2)
        val layoutManager2 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.layoutManager = layoutManager2
        adapter2 = MyAdapter(this, directorList)
        recyclerView2.adapter = adapter2
    }
}