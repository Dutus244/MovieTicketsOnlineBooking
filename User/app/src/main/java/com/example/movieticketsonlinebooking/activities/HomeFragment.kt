package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import android.graphics.Color
import com.example.movieticketsonlinebooking.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.movieticketsonlinebooking.fragments.Item
import com.example.movieticketsonlinebooking.fragments.MyGridAdapter
import com.example.movieticketsonlinebooking.fragments.The_Slide_Items_Model_Class_HomePage
import com.example.movieticketsonlinebooking.fragments.The_Slide_items_Pager_Adapter_HomePage
import com.google.android.material.tabs.TabLayout
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private var listItems: ArrayList<The_Slide_Items_Model_Class_HomePage>? = ArrayList()
    private var page: ViewPager? = null
    private var tabLayout: TabLayout? = null
    var adapter : MyGridAdapter? = null
    var gridView: GridView? = null
    var buttonCurrentFilm: Button? = null
    var buttonComingFilm: Button? = null
    var current: Int? = 1
    var arrayList: ArrayList<Item> = ArrayList()
    var buttonMore: Button? = null

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
    }

    fun getDataForSlider () {
        // Lấy 3 item mới nhất
        listItems!!.add(The_Slide_Items_Model_Class_HomePage(R.drawable.foreplay_background, "Bệnh viện Hoàn Mỹ"))
        listItems!!.add(The_Slide_Items_Model_Class_HomePage(R.drawable.foreplay_background, "Bệnh viện Hoàn Mỹ"))
        listItems!!.add(The_Slide_Items_Model_Class_HomePage(R.drawable.foreplay_background, "Bệnh viện Hoàn Mỹ"))
    }

    fun getDataForList () {
        arrayList.clear()
        if (current == 1) {
            // Ds phim hiện tại 6 phần tử
            arrayList.add(Item("hello",R.drawable.foreplay_background))
            arrayList.add(Item("hello",R.drawable.foreplay_background))
            arrayList.add(Item("hello",R.drawable.foreplay_background))
            arrayList.add(Item("hello",R.drawable.foreplay_background))
            arrayList.add(Item("hello",R.drawable.foreplay_background))
            arrayList.add(Item("hello",R.drawable.foreplay_background))
        }
        else {
            // Ds phim sắp chiếu
            arrayList.add(Item("hi",R.drawable.foreplay_background))
            arrayList.add(Item("hi",R.drawable.foreplay_background))
            arrayList.add(Item("hi",R.drawable.foreplay_background))
            arrayList.add(Item("hi",R.drawable.foreplay_background))
            arrayList.add(Item("hio",R.drawable.foreplay_background))
            arrayList.add(Item("hi",R.drawable.foreplay_background))
        }
        adapter?.notifyDataSetChanged()
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
        getDataForSlider()

        // Test list
        getDataForList ()

        buttonCurrentFilm = view.findViewById(R.id.activity_home_page_button_current_film)
        buttonComingFilm = view.findViewById(R.id.activity_home_page_button_comming_film)

        buttonComingFilm?.setOnClickListener {
            current = 0
            buttonComingFilm?.setTextColor(Color.parseColor("#FF0303"));
            buttonCurrentFilm?.setTextColor(Color.parseColor("#C8C8C8"));
            getDataForList ()
        }

        buttonCurrentFilm?.setOnClickListener {
            current = 1
            buttonComingFilm?.setTextColor(Color.parseColor("#C8C8C8"));
            buttonCurrentFilm?.setTextColor(Color.parseColor("#FF0303"));
            getDataForList ()
        }

        val itemsPager_adapter = The_Slide_items_Pager_Adapter_HomePage(requireActivity(), listItems)
        page!!.adapter = itemsPager_adapter

        val timer = Timer()
        timer.scheduleAtFixedRate(
            The_slide_timer(),
            2000,
            3000
        )
        tabLayout!!.setupWithViewPager(page, true)

        gridView = view.findViewById(R.id.activity_home_page_gridview_list_film)
        adapter = MyGridAdapter(requireActivity(), arrayList)
        gridView!!.adapter = adapter
        gridView!!.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(activity, FilmInfoActivity::class.java)
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