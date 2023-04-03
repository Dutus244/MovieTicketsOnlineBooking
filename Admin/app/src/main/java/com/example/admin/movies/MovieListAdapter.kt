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
        holder.position = position
        holder.nameText.text = list[position].title
        holder.dateText.text = "Release date: ${SimpleDateFormat("dd/MM/yyyy", 
            Locale.getDefault()).format(list[position].release_date)}"
        holder.activeText.text = ""
        if(list[position].is_active) holder.activeText.append("Currently active, ")
        else holder.activeText.append("Inactive, ")

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, EditMovie::class.java)
            intent.putExtra("cinema", list[position])
            activity.startActivityForResult(intent, RequestCode.MOVIE_SCREEN_EDIT)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}