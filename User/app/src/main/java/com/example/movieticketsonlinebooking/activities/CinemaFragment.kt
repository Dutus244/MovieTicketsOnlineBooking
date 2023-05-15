package com.example.movieticketsonlinebooking.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.Cinema
import com.example.movieticketsonlinebooking.viewmodels.Movie
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.GeoApiContext
import com.google.maps.DirectionsApi
import com.google.maps.model.TravelMode
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class CinemaFragment : Fragment(), TextWatcher {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var arrayList: ArrayList<Cinema> = ArrayList()
    var tempList: ArrayList<Cinema> = ArrayList()
    var adapter: MyAdapter? = null

    var cinemas: MutableList<Cinema> = listOf<Cinema>().toMutableList()


    class MyAdapter(private val context: Context, private val arrayList: java.util.ArrayList<Cinema>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewName: TextView = itemView.findViewById(R.id.textViewName)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val textViewAddress: TextView = itemView.findViewById(R.id.textViewAddress)
            val textViewPhone: TextView = itemView.findViewById(R.id.textViewPhone)
            val textViewDistance: TextView = itemView.findViewById(R.id.textViewDistance)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_list_view_cinema_layout, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = arrayList[position]
            holder.textViewName.text = data.name

            if (!data.img_url.isEmpty()) {
                Picasso.get().load(data.img_url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imageView)
            }
            holder.textViewAddress.text = data.address
            holder.textViewPhone.text = "0903552552"
            if (data.type == "Big") {
                holder.textViewDistance.text = "Phòng phim lớn"
            }
            else if (data.type == "Small") {
                holder.textViewDistance.text = "Phòng phim nhỏ"
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, CinemaFilmDetailActivity::class.java)
                intent.putExtra("cinema", data)
                intent.putExtra("cinema_id", data!!.id)
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return arrayList.size
        }

        fun updateData(newList: List<Cinema>) {
            arrayList.clear()
            arrayList.addAll(newList)
            notifyDataSetChanged()
        }
    }

    fun readData() {
        val db = Firebase.firestore
        db.collection("cinema")
            .whereEqualTo("status", "Open")
            .whereEqualTo("is_deleted", false)
            .get()
            .addOnSuccessListener { documents ->
                activity?.runOnUiThread {
                    cinemas.addAll(documents.toObjects(Cinema::class.java))
                    adapter?.updateData(cinemas)
                    tempList.addAll(cinemas)
                }
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error getting document", e)
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 404) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(
            R.layout.fragment_cinema,
            container, false
        )

        readData()

        val linearLayoutManager = LinearLayoutManager(requireActivity())
        val recyclerView: RecyclerView = view.findViewById(R.id.activity_cinema_customRecyclerView)
        recyclerView.layoutManager = linearLayoutManager
        adapter = MyAdapter(requireActivity(), arrayList)
        recyclerView.adapter = adapter

        // nếu dùng chức năng này sẽ bị delay vài s
//        updateDistance()

        var searchEditText: EditText = view.findViewById(R.id.activity_cinema_search_search)
        searchEditText.addTextChangedListener(this);

        return view
    }

    companion object {

    }

    var resultList: ArrayList<Cinema> = ArrayList()

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        resultList.clear()
        if (s != null && s.length > 0) {
            val query = s.toString().uppercase()
            val filters: java.util.ArrayList<Cinema> =
                java.util.ArrayList<Cinema>()
            for (i in 0 until tempList.size) {
                val name: String = tempList.get(i).name
                if (name.uppercase().contains(query)) {
                    filters.add(tempList.get(i))
                }
            }
            resultList.addAll(filters)
        } else {
            for (i in 0 until tempList.size) {
                resultList.add(tempList.get(i))
            }
        }
        for (i in resultList) {
            println(i.name)
        }
        adapter!!.updateData(resultList)
    }

    override fun afterTextChanged(s: Editable?) {

    }
}