package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.movieticketsonlinebooking.R

class BookSeatActivity : AppCompatActivity() {
    var buttonConfirm: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_seat)

        val seatGridLayout = findViewById<GridLayout>(R.id.seat_grid)
        val seatPositions = arrayOf(
            "11001111111111",
            "11001111111111",
            "11001111111111",
            "11001111111111",
            "11001111111111",
            "11001111111111",
            "11001111111111",
            "11001111111111",
            "11001111111111"
            // add more rows as needed
        )


        for (i in 0 until seatPositions.size) {
            val row = seatPositions[i]
            for (j in 0 until row.length) {
                val imageView = ImageView(this)
                val layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(i),
                    GridLayout.spec(j)
                )
                layoutParams.width = 50
                layoutParams.height = 50
                layoutParams.setMargins(2, 4, 2, 4)
                imageView.layoutParams = layoutParams
                if (row[j] == '1') {
                    imageView.setImageResource(R.drawable.ic_seat_available)
                    imageView.tag = "available"
                    imageView.setOnClickListener {
                        if (imageView.tag == "available") {
                            imageView.setImageResource(R.drawable.ic_seat_selected)
                            imageView.tag = "selected"
                        } else {
                            imageView.setImageResource(R.drawable.ic_seat_available)
                            imageView.tag = "available"
                        }
                    }
                } else if (row[j] == '2') {
                    imageView.setImageResource(R.drawable.ic_seat_sold)
                    imageView.tag = "sold"
                }
                else {
                    imageView.setImageResource(R.drawable.ic_seat_unavailable)
                    imageView.tag = "unavailable"
                }
                seatGridLayout.addView(imageView)
            }
        }

        val seatNameGridLayout1 = findViewById<GridLayout>(R.id.name_seat_grid1)
        var c: Char = 'A'
        seatNameGridLayout1.columnCount = 1 // set column count to 1
        for (i in 0 until seatPositions.size) {
            val row = seatPositions[i]
            val textView = TextView(this)
            val layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(i),
                GridLayout.spec(0)
            )
            layoutParams.width = 50
            layoutParams.height = 50
            layoutParams.setMargins(0, 4, 0, 4)
            if (row[0] == '1') {
                textView.setText("$c")
                ++c
            }
            seatNameGridLayout1.addView(textView, layoutParams)
        }

        val seatNameGridLayout2 = findViewById<GridLayout>(R.id.name_seat_grid2)
        var d: Char = 'A'
        seatNameGridLayout2.columnCount = 1 // set column count to 1
        for (i in 0 until seatPositions.size) {
            val row = seatPositions[i]
            val textView = TextView(this)
            val layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(i),
                GridLayout.spec(0)
            )
            layoutParams.width = 50
            layoutParams.height = 50
            layoutParams.setMargins(0, 4, 0, 4)
            if (row[0] == '1') {
                textView.setText("$d")
                ++d
            }
            seatNameGridLayout2.addView(textView, layoutParams)
        }

        buttonConfirm = findViewById(R.id.activity_book_seat_button_confirm)
        buttonConfirm?.setOnClickListener {
            val intent = Intent(applicationContext, PayActivity::class.java)
            startActivity(intent)
        }
    }
}