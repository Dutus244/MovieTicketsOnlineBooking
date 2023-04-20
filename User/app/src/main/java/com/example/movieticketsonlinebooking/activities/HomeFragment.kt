package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import android.graphics.Color
import com.example.movieticketsonlinebooking.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.movieticketsonlinebooking.fragments.MyGridAdapter
import com.example.movieticketsonlinebooking.fragments.The_Slide_Items_Model_Class_HomePage
import com.example.movieticketsonlinebooking.fragments.The_Slide_items_Pager_Adapter_HomePage
import com.example.movieticketsonlinebooking.viewmodels.Movie
import com.google.android.material.tabs.TabLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private var listItems: ArrayList<The_Slide_Items_Model_Class_HomePage>? = ArrayList()
    private var page: ViewPager? = null
    private var tabLayout: TabLayout? = null
    var adapter: MyGridAdapter? = null
    var gridView: GridView? = null
    var buttonCurrentFilm: Button? = null
    var buttonComingFilm: Button? = null
    var current: Int? = 1
    var buttonMore: Button? = null

    var moviesList: MutableList<Movie> = listOf<Movie>().toMutableList()
    var sliderMovies: MutableList<Movie> = listOf<Movie>().toMutableList()

    inner class The_slide_timer : TimerTask() {
        override fun run() {
            val activity = activity
            if (activity != null) {
                activity.runOnUiThread {
                    if (page!!.currentItem < listItems!!.size - 1) {
                        page!!.currentItem = page!!.currentItem + 1
                    } else {
                        page!!.currentItem = 0
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
    }

    fun getMovieForSlider() {
        // Lấy 3 item mới nhất
        val db = Firebase.firestore
        db.collection("movie")
            .whereEqualTo("is_active", true)
            .whereEqualTo("is_deleted", false)
            .orderBy("rating", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { documents ->
                sliderMovies = documents.toObjects(Movie::class.java)
                listItems = java.util.ArrayList()
                for (element in sliderMovies) {
                    listItems!!.add(
                        The_Slide_Items_Model_Class_HomePage(
                            element.poster_url,
                            element.title
                        )
                    )
                }
                activity?.runOnUiThread {
                    page!!.adapter =
                        The_Slide_items_Pager_Adapter_HomePage(requireActivity(), listItems)
                }
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error getting document", e)
            }
    }

    fun getDataForList() {
        moviesList.clear()
        if (current == 1) {
            // Ds phim hiện tại 6 phần tử
            val db = Firebase.firestore
            db.collection("movie")
                .whereEqualTo("is_active", true)
                .whereEqualTo("is_deleted", false)
                .whereLessThan("release_date", Date())
                .orderBy("release_date", Query.Direction.DESCENDING)
                .limit(6)
                .get()
                .addOnSuccessListener { documents ->
                    activity?.runOnUiThread {
                        moviesList.addAll(documents.toObjects(Movie::class.java))
                        adapter?.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("DB", "Error getting document", e)
                }
        } else {
            // Ds phim sắp chiếu
            val db = Firebase.firestore
            db.collection("movie")
                .whereEqualTo("is_active", true)
                .whereEqualTo("is_deleted", false)
                .whereGreaterThan("release_date", Date())
                .orderBy("release_date", Query.Direction.ASCENDING)
                .limit(6)
                .get()
                .addOnSuccessListener { documents ->
                    activity?.runOnUiThread {
                        moviesList.addAll(documents.toObjects(Movie::class.java))
                        adapter?.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("DB", "Error getting document", e)
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_home,
            container, false
        )

        page = view.findViewById(R.id.activity_home_page_my_pager)
        tabLayout = view.findViewById(R.id.activity_home_page_my_tablayout)

        // Test slide
        getMovieForSlider()

        // Test list
        getDataForList()

        buttonCurrentFilm = view.findViewById(R.id.activity_home_page_button_current_film)
        buttonComingFilm = view.findViewById(R.id.activity_home_page_button_comming_film)

        buttonComingFilm?.setOnClickListener {
            current = 0
            buttonComingFilm?.setTextColor(Color.parseColor("#FF0303"));
            buttonCurrentFilm?.setTextColor(Color.parseColor("#C8C8C8"));
            getDataForList()
        }

        buttonCurrentFilm?.setOnClickListener {
            current = 1
            buttonComingFilm?.setTextColor(Color.parseColor("#C8C8C8"));
            buttonCurrentFilm?.setTextColor(Color.parseColor("#FF0303"));
            getDataForList()
        }

        val itemsPager_adapter =
            The_Slide_items_Pager_Adapter_HomePage(requireActivity(), listItems)
        page!!.adapter = itemsPager_adapter

        val timer = Timer()
        timer.scheduleAtFixedRate(
            The_slide_timer(),
            2000,
            3000
        )
        tabLayout!!.setupWithViewPager(page, true)

        gridView = view.findViewById(R.id.activity_home_page_gridview_list_film)
        adapter = MyGridAdapter(requireActivity(), moviesList)
        gridView!!.adapter = adapter
        gridView!!.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(activity, FilmInfoActivity::class.java)
            intent.putExtra("movie", moviesList[i])
            startActivity(intent)
        }

        buttonMore = view.findViewById(R.id.activity_home_page_list_fim_more)
        buttonMore?.setOnClickListener {
            val intent = Intent(activity, FilmListActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    companion object {

    }
}