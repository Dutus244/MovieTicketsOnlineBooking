package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import com.example.movieticketsonlinebooking.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.movieticketsonlinebooking.fragments.Item
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
    private var listItems: ArrayList<The_Slide_Items_Model_Class_HomePage>? = null
    private var page: ViewPager? = null
    private var tabLayout: TabLayout? = null
    var adapter: MyGridAdapter? = null
    var gridView: GridView? = null

    var sliderMovies: List<Movie> = listOf()
    var airingMovies: List<Movie> = listOf()

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

        val db = Firebase.firestore
        // Get slider movies
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
                            R.drawable.foreplay_background,
                            element.title
                        )
                    )
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
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error getting document", e)
            }

        // Get airing movies
        db.collection("movie")
            .whereEqualTo("is_active", true)
            .whereEqualTo("is_deleted", false)
            .orderBy("release_date", Query.Direction.DESCENDING)
            .limit(6)
            .get()
            .addOnSuccessListener { documents ->
                airingMovies = documents.toObjects(Movie::class.java)
                var arrayList: ArrayList<Item> = ArrayList()
                for (element in airingMovies) {
                    arrayList.add(Item(element.title, R.drawable.foreplay_background))
                    gridView = view.findViewById(R.id.activity_home_page_gridview_list_film)
                    adapter = MyGridAdapter(requireActivity(), arrayList)
                    gridView!!.adapter = adapter
                    gridView!!.setOnItemClickListener { adapterView, view, i, l ->
                        val intent = Intent(activity, FilmInfoActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error getting document", e)
            }

        return view
    }

    companion object {

    }
}