package com.example.movieticketsonlinebooking.activities

import android.R
import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.tabs.TabLayout
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private var listItems: ArrayList<The_Slide_Items_Model_Class_HomePage>? = null
    private var page: ViewPager? = null
    private var tabLayout: TabLayout? = null
    var adapter : MyGridAdapter? = null
    var gridView: GridView? = null

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            com.example.movieticketsonlinebooking.R.layout.fragment_home,
            container, false
        )
        page = view.findViewById(com.example.movieticketsonlinebooking.R.id.activity_home_page_my_pager)
        tabLayout = view.findViewById(com.example.movieticketsonlinebooking.R.id.activity_home_page_my_tablayout)

        // Test slide
        listItems = java.util.ArrayList()
        listItems!!.add(The_Slide_Items_Model_Class_HomePage(com.example.movieticketsonlinebooking.R.drawable.foreplay_background, "Bệnh viện Hoàn Mỹ"))
        listItems!!.add(The_Slide_Items_Model_Class_HomePage(com.example.movieticketsonlinebooking.R.drawable.foreplay_background, "Bệnh viện Hoàn Mỹ"))
        listItems!!.add(The_Slide_Items_Model_Class_HomePage(com.example.movieticketsonlinebooking.R.drawable.foreplay_background, "Bệnh viện Hoàn Mỹ"))
        listItems!!.add(The_Slide_Items_Model_Class_HomePage(com.example.movieticketsonlinebooking.R.drawable.foreplay_background, "Bệnh viện Hoàn Mỹ"))

        val itemsPager_adapter = The_Slide_items_Pager_Adapter_HomePage(requireActivity(), listItems)
        page!!.adapter = itemsPager_adapter

        val timer = Timer()
        timer.scheduleAtFixedRate(
            The_slide_timer(),
            2000,
            3000
        )
        tabLayout!!.setupWithViewPager(page, true)

        var arrayList: ArrayList<Item> = ArrayList()

        // Test
        arrayList.add(Item("hello",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Item("hello",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Item("hello",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Item("hello",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Item("hello",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Item("hello",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))

        gridView = view.findViewById(com.example.movieticketsonlinebooking.R.id.activity_home_page_gridview_list_film)
        adapter = MyGridAdapter(requireActivity(), arrayList)
        gridView!!.adapter = adapter
        gridView!!.setOnItemClickListener { adapterView, view, i, l ->

        }
        return view
    }

    companion object {

    }
}