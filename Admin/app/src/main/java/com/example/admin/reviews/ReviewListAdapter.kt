package com.example.admin.reviews

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Int

class ReviewListAdapter(private val activity: Activity, private var list: List<Review>) :
    RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        var position: Int? = null
        val tvNamaPasien = listItemView.findViewById(R.id.tv_nama_pasien) as TextView
        val totalStarRating = listItemView.findViewById(R.id.total_star_rating) as RatingBar
        val tvTglRating = listItemView.findViewById(R.id.tv_tgl_rating) as TextView
        val tvDescReview = listItemView.findViewById(R.id.tv_desc_review) as TextView
        val btnDel =  listItemView.findViewById(R.id.btn_delete) as Button
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
        holder.totalStarRating.rating = (list[position].rating).toString().toFloat()
        holder.tvDescReview.text = list[position].detail
        holder.tvTglRating.setText(
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss",
            Locale.getDefault()).format(list[position].date))
        holder.tvNamaPasien.text = list[position].user_name
        holder.btnDel.setOnClickListener {
            Log.e("bucu", "bucu")
            createDeleteDialog(list[position].id)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun createDeleteDialog(review_id: String): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Bạn có chắc là muốn xóa!")
            .setPositiveButton("Có") { dialog, id ->
                val db = Firebase.firestore
                db.collection("review")
                    .document(review_id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            activity,
                            "Xóa thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        val mutableList = list.toMutableList()
                        mutableList.removeIf { it.id == review_id }
                        list = mutableList.toList()
                        notifyDataSetChanged()
                    }
                    .addOnFailureListener { exception ->
                        Log.w("DB", "Error getting documents.", exception)
                    }
            }
            .setNegativeButton("Không") { dialog, id ->

            }
        return builder.create().apply {
            show()
        }
    }
}