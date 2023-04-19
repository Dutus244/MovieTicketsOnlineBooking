package com.example.movieticketsonlinebooking.fragments

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.Movie

class MyGridAdapter (private var context: Context, private var items:
List<Movie>) : BaseAdapter() {
    private class ViewHolder(row: View?) {
        var logoImgV: ImageView? = null
        var textView: TextView? = null
        init {
            logoImgV = row?.findViewById(R.id.idPicture)
            textView = row?.findViewById(R.id.idName)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            view = (inflater as LayoutInflater).inflate(R.layout.custom_layout_gridview_list_films, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.logoImgV?.setImageResource(R.drawable.foreplay_background)
        viewHolder.textView?.text = items[position].title
        return view
    }

    override fun getItem(i: Int): Movie {
        return items[i]
    }
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
    override fun getCount(): Int {
        return items.size
    }
}