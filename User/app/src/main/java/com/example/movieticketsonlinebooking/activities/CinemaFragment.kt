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

            Picasso.get().load(data.img_url).into(holder.imageView)
            holder.textViewAddress.text = data.address
            holder.textViewPhone.text = "0903552552"
//            if (data.distance != "") {
//                holder.textViewDistance.text = data.distance + " km"
//            }
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

//    fun sortCinemasByDistanceAsc(cinemas: ArrayList<Cinema>) {
//        cinemas.sortBy { it.distance }
//    }

//    fun updateDistance() {
//        var tempDisList: ArrayList<Cinema> = ArrayList()
//        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
//                val location =
//                    locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER) // or LocationManager.NETWORK_PROVIDER
//                if (location != null) {
//                    val userLatitude = location.latitude
//                    val userLongitude = location.longitude
//                    val geocoder = Geocoder(requireContext())
//                    for (i in 0 until arrayList.size) {
//                        val results = geocoder.getFromLocationName(arrayList[i].name, 1)
//                        if (results != null && results.isNotEmpty()) {
//                            val placeLatitude = results[0].latitude
//                            val placeLongitude = results[0].longitude
//
//                            val origin = "$userLatitude,$userLongitude"
//                            val destination = "$placeLatitude,$placeLongitude"
//
//                            // Calculate distance
//                            val resultsArray = FloatArray(1)
//                            Location.distanceBetween(userLatitude, userLongitude, placeLatitude, placeLongitude, resultsArray)
//                            val distance = resultsArray[0] / 1000// Distance in meters
//                            val roundedDistance = String.format("%.1f", distance)
//                            tempDisList.add(
//                                Cinema(
//                                    arrayList.get(i).id,
//                                    arrayList.get(i).name,
//                                    arrayList.get(i).address,
//                                    arrayList.get(i).phone,
//                                    arrayList.get(i).avatar,
//                                    roundedDistance
//                                )
//                            )
//                        }
//                    }
//                    sortCinemasByDistanceAsc(tempDisList)
//                    tempList.clear()
//                    tempList.addAll(tempDisList)
//                    adapter!!.updateData(tempDisList)
//                }
//            }
//    }


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

        // Check if location permission is granted
        if (activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Location permission is already granted, you can proceed with getting the user's location
            // and calculating distance as shown in the previous example
        } else {
            // Location permission is not granted, request permission from the user
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 404)
        }

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