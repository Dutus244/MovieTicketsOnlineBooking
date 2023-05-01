package com.example.admin.screenings

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
import com.example.admin.cinemas.Cinema
import com.example.admin.cinemas.EditCinema

class CinemaScreeningListAdapter(private val activity: Activity, private val list: List<Cinema>) :
    RecyclerView.Adapter<CinemaScreeningListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        var position: Int? = null
        val imageView = listItemView.findViewById(R.id.cinemaImg) as ImageView
        val nameText = listItemView.findViewById(R.id.cinemaNameTV) as TextView
        val addrText = listItemView.findViewById(R.id.cinemaAddrTV) as TextView
        val audiNumText = listItemView.findViewById(R.id.cinemaAudiNumTV) as TextView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CinemaScreeningListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val cinemaView = inflater.inflate(R.layout.cinema_item, parent, false)
        // Return a new holder instance
        return ViewHolder(cinemaView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.position = position
        holder.nameText.text = list[position].name
        holder.addrText.text = list[position].address
        holder.audiNumText.append(list[position].auditoriums_no.toString())
        holder.itemView.setOnClickListener {
            val intent = Intent(activity, ScreeningList::class.java)
            intent.putExtra("cinema", list[position])
            activity.startActivityForResult(intent, RequestCode.SCREENING_SCREEN_DETAIL)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}