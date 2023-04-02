package com.example.admin.cinemas

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.LayoutInflaterFactory
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.RequestCode

class CinemaListAdapter(private val activity: Activity, private val list: List<Cinema>) :
    RecyclerView.Adapter<CinemaListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        var position: Int? = null
        val imageView: ImageView = listItemView.findViewById(R.id.cinemaImg) as ImageView
        val nameText = listItemView.findViewById(R.id.cinemaNameTV) as TextView
        val addrText = listItemView.findViewById(R.id.cinemaAddrTV) as TextView
        val audiNumText = listItemView.findViewById(R.id.cinemaAudiNumTV) as TextView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CinemaListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val studentView = inflater.inflate(R.layout.cinema_item, parent, false)
        // Return a new holder instance
        return ViewHolder(studentView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.position = position
        holder.nameText.text = list[position].name
        holder.addrText.text = list[position].address
        holder.audiNumText.append(list[position].auditoriums_no.toString())
        holder.itemView.setOnClickListener {
            val intent = Intent(activity, EditCinema::class.java)
            intent.putExtra("cinema", list[position])
            activity.startActivityForResult(intent, RequestCode.CINEMA_SCREEN_EDIT)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}