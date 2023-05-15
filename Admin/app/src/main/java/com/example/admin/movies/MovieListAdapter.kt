package com.example.admin.movies

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.RequestCode
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class MovieListAdapter(private val activity: Activity, private val list: List<Movie>) :
    RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        var position: Int? = null
        val imageView: ImageView = listItemView.findViewById(R.id.movieImg) as ImageView
        val nameText = listItemView.findViewById(R.id.movieNameTV) as TextView
        val dateText = listItemView.findViewById(R.id.movieReleasedDateTV) as TextView
        val activeText = listItemView.findViewById(R.id.movieActiveTV) as TextView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val movieView = inflater.inflate(R.layout.movie_item, parent, false)
        return ViewHolder(movieView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picasso = Picasso.get()
        holder.position = position
        if (!list[position].poster_url.isEmpty()) {
            picasso.load(list[position].poster_url)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imageView)
        }
        holder.nameText.text = list[position].title
        holder.dateText.text = "Ngày phát hành: ${SimpleDateFormat("dd/MM/yyyy", 
            Locale.getDefault()).format(list[position].release_date)}"
        holder.activeText.text = ""
        if(list[position].is_active) holder.activeText.append("Hiện đang chiếu")
        else holder.activeText.append("Hiện không chiếu")

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, EditMovie::class.java)
            intent.putExtra("movie", list[position])
            activity.startActivityForResult(intent, RequestCode.MOVIE_SCREEN_EDIT)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}