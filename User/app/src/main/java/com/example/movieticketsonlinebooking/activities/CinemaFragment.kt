package com.example.movieticketsonlinebooking.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieticketsonlinebooking.R

class CinemaFragment : Fragment(), TextWatcher {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var arrayList: ArrayList<Cinema> = ArrayList()
    var tempList: ArrayList<Cinema> = ArrayList()
    var adapter: MyAdapter? = null

    class Cinema(var id: String, var name: String, var address: String, var phone: String, var avatar: Int )

    class MyAdapter(private val context: Context, private val arrayList: java.util.ArrayList<Cinema>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewName: TextView = itemView.findViewById(R.id.textViewName)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val textViewAddress: TextView = itemView.findViewById(R.id.textViewAddress)
            val textViewPhone: TextView = itemView.findViewById(R.id.textViewPhone)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_list_view_cinema_layout, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = arrayList[position]
            holder.textViewName.text = data.name
            holder.imageView.setImageResource(data.avatar)
            holder.textViewAddress.text = data.address
            holder.textViewPhone.text = data.phone
            holder.itemView.setOnClickListener {
                val intent = Intent(context, CinemaFilmDetailActivity::class.java)
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
        arrayList.add(Cinema("1","Galaxy Nguyễn Du","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","Galaxy Nguyễn Trãi","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","Cinestar Nguyễn Du","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","Galaxy Tân Bình","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","Cienstar Hai Bà Trưng","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","BHD Tân Bình","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","BHD Quang Trung","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","Galaxy Nguyễn Du","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","Galaxy Nguyễn Du","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","Galaxy Nguyễn Du","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","Galaxy Nguyễn Du","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","Galaxy Nguyễn Du","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))
        arrayList.add(Cinema("1","Galaxy Nguyễn Du","123","456",com.example.movieticketsonlinebooking.R.drawable.foreplay_background))

        tempList.addAll(arrayList)
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
                    filters.add(
                        Cinema(
                            tempList.get(i).id,
                            tempList.get(i).name,
                            tempList.get(i).address,
                            tempList.get(i).phone,
                            tempList.get(i).avatar
                        )
                    )
                }
            }
            resultList.addAll(filters)
        } else {
            for (i in 0 until tempList.size) {
                resultList.add(
                    Cinema(
                        tempList.get(i).id,
                        tempList.get(i).name,
                        tempList.get(i).address,
                        tempList.get(i).phone,
                        tempList.get(i).avatar
                    )
                )
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