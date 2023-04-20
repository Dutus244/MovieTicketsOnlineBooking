package com.example.admin.reviews

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
import com.example.admin.movies.Movie
import java.text.SimpleDateFormat
import java.util.*

class ReviewListAdapter(private val activity: Activity, private val list: List<Review>) :
    RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        var position: Int? = null
        val imageView: ImageView = listItemView.findViewById(R.id.userImg) as ImageView
        val nameText = listItemView.findViewById(R.id.reviewUserNameTV) as TextView
        val ratingText = listItemView.findViewById(R.id.reviewRatingTV) as TextView
        val dateText = listItemView.findViewById(R.id.reviewDateTV) as TextView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val movieView = inflater.inflate(R.layout.review_item, parent, false)
        return ViewHolder(movieView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.position = position
        holder.nameText.text = list[position].user_name
        holder.ratingText.text = list[position].rating.toString()
        holder.dateText.text = SimpleDateFormat("dd/MM/yyyy",
                Locale.getDefault()).format(list[position].date)

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, ReviewDetail::class.java)
            intent.putExtra("review", list[position])
            activity.startActivityForResult(intent, RequestCode.REVIEW_SCREEN_DETAIL)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}