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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ScreeningListAdapter(private val activity: Activity, private val list: List<Screening>) :
    RecyclerView.Adapter<ScreeningListAdapter.ViewHolder>()  {
        inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
            var position: Int? = null
            val imageView: ImageView = listItemView.findViewById(R.id.screeningImg) as ImageView
            val nameText = listItemView.findViewById(R.id.screeningNameTV) as TextView
            val audiNameText = listItemView.findViewById(R.id.screeningAuditoriumNameTV) as TextView
            val timeStartText = listItemView.findViewById(R.id.screeningTimeStartTV) as TextView
            val timeEndText = listItemView.findViewById(R.id.screeningTimeEndTV) as TextView
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
            holder.position = position
            holder.nameText.text = list[position].movie_name
            holder.audiNameText.text = "Tên phòng chiếu: ${list[position].auditorium_name}"
            holder.timeStartText.text = "Bắt đầu: ${SimpleDateFormat("dd/MM/yyyy hh:mm",
                Locale.getDefault()).format(list[position].screening_start.seconds*1000)}"
            holder.timeEndText.text = "Kết thúc: ${SimpleDateFormat("dd/MM/yyyy hh:mm",
                Locale.getDefault()).format(list[position].screening_end.seconds*1000)}"

            holder.itemView.setOnClickListener {
                val intent = Intent(activity, EditScreening::class.java)
                intent.putExtra("screening", list[position])
                activity.startActivityForResult(intent, RequestCode.SCREENING_SCREEN_EDIT)
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
}