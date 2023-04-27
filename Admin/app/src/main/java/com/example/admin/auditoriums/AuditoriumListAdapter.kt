package com.example.admin.auditoriums

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
import com.example.admin.seats.SeatScreenTypeBig
import com.example.admin.seats.SeatScreenTypeSmall

class AuditoriumListAdapter(private val activity: Activity, private val list: List<Auditorium>) :
    RecyclerView.Adapter<AuditoriumListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        var position: Int? = null
        val imageView = listItemView.findViewById(R.id.auditoriumImg) as ImageView
        val nameText = listItemView.findViewById(R.id.auditoriumNameTV) as TextView
        val audiNumText = listItemView.findViewById(R.id.auditoriumNumTV) as TextView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AuditoriumListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val auditoriumView = inflater.inflate(R.layout.auditorium_item, parent, false)
        // Return a new holder instance
        return ViewHolder(auditoriumView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.position = position
        holder.nameText.text = list[position].name
        when (list[position].type) {
            "Big" -> {
                holder.audiNumText.append(list[position].seats_no.toString())
            }
            "Small" -> {
                holder.audiNumText.visibility = View.GONE
            }
        }
        holder.itemView.setOnClickListener {
            when (list[position].type) {
                "Big" -> {
                    val intent = Intent(activity, SeatScreenTypeBig::class.java)
                    intent.putExtra("auditorium", list[position])
                    activity.startActivityForResult(intent, RequestCode.AUDITORIUM_BIG_SCREEN_EDIT)
                }
                "Small" -> {
                    val intent = Intent(activity, SeatScreenTypeSmall::class.java)
                    intent.putExtra("auditorium", list[position])
                    activity.startActivityForResult(intent, RequestCode.AUDITORIUM_SMALL_SCREEN_EDIT)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}