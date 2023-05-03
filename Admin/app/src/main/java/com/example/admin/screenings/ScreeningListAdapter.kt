package com.example.admin.screenings

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.RequestCode
import com.squareup.picasso.Picasso
import java.lang.Math.ceil
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class ScreeningListAdapter(private val activity: Activity, private var list: List<MovieScreening>) :
    RecyclerView.Adapter<ScreeningListAdapter.ViewHolder>()  {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        var position: Int? = null
        val imageView: ImageView = listItemView.findViewById(R.id.movieAvatar) as ImageView
        val nameText = listItemView.findViewById(R.id.movieName) as TextView
        val ratingText = listItemView.findViewById(R.id.movieRating) as TextView
        val classText = listItemView.findViewById(R.id.movieClass) as TextView
        val durationText = listItemView.findViewById(R.id.movieDuration) as TextView
        val showtimesButton = listItemView.findViewById(R.id.showtimeBtn) as Button
        val showtimesGridView = listItemView.findViewById(R.id.showtimeGridview) as GridView
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScreeningListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val screeningView = inflater.inflate(R.layout.screening_item, parent, false)
        return ViewHolder(screeningView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picasso = Picasso.get()
        holder.position = position
        picasso.load(list[position].poster_url).into(holder.imageView)
        holder.nameText.text = list[position].title
        holder.ratingText.text = list[position].rating.toString()
        holder.classText.text = list[position].classification
        holder.durationText.text = list[position].duration.toString()

        holder.showtimesButton.setOnClickListener {
            if (holder.showtimesGridView.visibility == View.VISIBLE) {
                holder.showtimesGridView.visibility = View.GONE
                holder.showtimesButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_left_24)
            } else {
                holder.showtimesGridView.visibility = View.VISIBLE
                holder.showtimesButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24)
            }
        }

        val adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, list[position].screenings.map {
            "${SimpleDateFormat("HH:mm", Locale.getDefault()).format(it.screening_start)}-${SimpleDateFormat("HH:mm", Locale.getDefault()).format(it.screening_end)}"
        })
        holder.showtimesGridView.horizontalSpacing = 5
        holder.showtimesGridView.verticalSpacing = 5
        holder.showtimesGridView.adapter = adapter

        val itemHeight = try {
            val itemView = adapter.getView(0, null, holder.showtimesGridView)
            itemView.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )
            itemView.measuredHeight
        } catch (e: Exception) {
            0
        }
        val numRows = ceil(list[position].screenings.size / 3.0).toInt()
        val gridViewHeight = numRows * itemHeight + 10
        holder.showtimesGridView.layoutParams.height = gridViewHeight

        holder.showtimesGridView.setOnItemClickListener { _, _, index, _ ->
            val intent = Intent(activity, EditScreening::class.java)
            intent.putExtra("screening", list[position].screenings[index])
            activity.startActivityForResult(intent, RequestCode.SCREENING_SCREEN_EDIT)
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
    fun update() {
        notifyDataSetChanged()
    }
}