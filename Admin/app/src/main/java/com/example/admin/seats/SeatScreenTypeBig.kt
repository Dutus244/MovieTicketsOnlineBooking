package com.example.admin.seats

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.admin.R
import com.example.admin.auditoriums.Auditorium
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.jahidhasanco.seatbookview.SeatBookView
import dev.jahidhasanco.seatbookview.SeatClickListener

class SeatScreenTypeBig : AppCompatActivity() {
    private lateinit var seatBookView: SeatBookView

    var title: TextView? = null
    var changeNameBtn: Button? = null
    var seatBtn: Button? = null
    var instructionTV: TextView? = null

    var auditorium: Auditorium? = null

    var row: Int = 0
    var col: Int = 0
    var seats: MutableList<Seat> = mutableListOf()
    var seatsRemove: List<Int> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_screen_type_big)
        FirebaseApp.initializeApp(this)


        intent = intent
        auditorium = intent.getSerializableExtra("auditorium") as? Auditorium

        title = findViewById(R.id.seatScreenBigTitle)
        changeNameBtn = findViewById(R.id.changeNameBtn)
        seatBtn = findViewById(R.id.seatBtn)
        instructionTV = findViewById(R.id.instructionTV)
        title!!.text = auditorium!!.name
        if (auditorium!!.map.isEmpty()) {
            seatBtn!!.text = "Tạo sơ đồ"
        } else {
            seatBtn!!.text = "Xóa"
            seatBtn!!.setBackgroundColor(Color.parseColor("#df4759"))

            val seats = makeExistedSeats(auditorium!!.map)
            val titles = makeExistedTitles(auditorium!!.map)
            seatBookView = findViewById(R.id.layoutSeat)
            seatBookView.setSeatsLayoutString(seats)
                .isCustomTitle(true)
                .setCustomTitle(titles)
                .setSeatLayoutPadding(2)
                .setSeatSize(400)
                .setSelectSeatLimit(0)
            seatBookView.show()
        }
        changeNameBtn!!.setOnClickListener {
            val dialog = createChangeNameDialog()
            dialog.show()
        }
        seatBtn!!.setOnClickListener {
            when (seatBtn!!.text) {
                "Tạo sơ đồ" -> {
                    val dialog = createMapDialog()
                    dialog.show()
                }
                "Lưu" -> {
                    val seats = IntArray(row * col) { (it + 1) }
                    val newSeats =
                        seats.map { if (it in seatsRemove) 0 else it }
                            .map { if (it != 0) 1 else 0 }
                            .toIntArray()
                    val seatMap = arrayListOf<String>()
                    for (i in 1..row) {
                        var line = ""
                        for (j in 1..col) {
                            line += newSeats[col * (i - 1) + (j - 1)].toString()
                        }
                        seatMap.add(line)
                    }
                    val seats_no = row * col - seatsRemove.size
                    createSeatMap(seatMap, seats_no)
                }
                "Xóa" -> {
                    val dialog = createDeleteDialog()
                    dialog.show()
                }
            }
        }


    }

    fun createChangeNameDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this@SeatScreenTypeBig)
        val input = EditText(this@SeatScreenTypeBig)
        input.setSingleLine()
        input.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(20))
        builder.setView(input)
        builder.setMessage("Nhập tên phòng chiếu")
            .setPositiveButton("Đổi") { _, _ ->
                val name = input.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val db = Firebase.firestore
                    db.collection("auditorium")
                        .document(auditorium!!.id)
                        .update("name", name)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@SeatScreenTypeBig,
                                "Đổi thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                            val replyIntent = Intent()
                            setResult(Activity.RESULT_OK, replyIntent)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Log.w("DB", "Error getting documents.", exception)
                        }
                }
            }
            .setNegativeButton("Hủy") { _, _ ->

            }
        return builder.create()
    }

    fun createMapDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this@SeatScreenTypeBig)
        val parentLayout = LinearLayout(this@SeatScreenTypeBig)
        parentLayout.orientation = LinearLayout.VERTICAL

        val rowInput = EditText(this@SeatScreenTypeBig)
        rowInput.inputType = InputType.TYPE_CLASS_NUMBER
        rowInput.hint = "Số dòng"
        rowInput.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(2))
        parentLayout.addView(rowInput)

        val colInput = EditText(this@SeatScreenTypeBig)
        colInput.inputType = InputType.TYPE_CLASS_NUMBER
        colInput.hint = "Số cột"
        colInput.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(2))
        parentLayout.addView(colInput)

        builder.setView(parentLayout)
        builder.setMessage("Nhập số dòng và số cột")
            .setPositiveButton("Tạo") { dialog, id ->
                row = rowInput.text.toString().toInt()
                col = colInput.text.toString().toInt()
                if (rowInput.text.isEmpty() && colInput.text.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT)
                        .show()
                } else if (row > 20 || col > 20) {
                    Toast.makeText(this, "Số quá lớn", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val seats = makeSeats(row, col)
                    val titles = makeTitles(row, col)
                    seatBookView = findViewById(R.id.layoutSeat)
                    seatBookView.setSeatsLayoutString(seats)
                        .isCustomTitle(true)
                        .setCustomTitle(titles)
                        .setSeatLayoutPadding(2)
                        .setSeatSize(400)
                    seatBookView.setSeatClickListener(object : SeatClickListener {
                        override fun onAvailableSeatClick(selectedIdList: List<Int>, view: View) {
                            seatsRemove = selectedIdList
                        }

                        override fun onBookedSeatClick(view: View) {
                        }

                        override fun onReservedSeatClick(view: View) {
                        }
                    })
                    seatBookView.show()

                    seatBtn!!.text = "Lưu"
                    instructionTV!!.visibility = View.VISIBLE
                }
            }
            .setNegativeButton("Hủy") { dialog, id ->

            }
        return builder.create()
    }

    fun createDeleteDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this@SeatScreenTypeBig)
        builder.setMessage("Bạn có chắc là muốn xóa!")
            .setPositiveButton("Có") { dialog, id ->
                val db = Firebase.firestore
                db.collection("auditorium")
                    .document(auditorium!!.id)
                    .update("is_deleted", true)
                    .addOnSuccessListener {
                        db.collection("cinema")
                            .document(auditorium!!.cinema_id)
                            .update("auditoriums_no", FieldValue.increment(-1))
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Xóa thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val replyIntent = Intent()
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w("DB", "Error getting documents.", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w("DB", "Error getting documents.", e)
                    }

            }
            .setNegativeButton("Không") { dialog, id ->

            }
        return builder.create()
    }

    fun makeSeats(row: Int, col: Int): String {
        var seats = ""
        for (i in 1..row) {
            seats += "/"
            seats += "A".repeat(col)
        }
        return seats
    }

    fun makeTitles(row: Int, col: Int): List<String> {
        val title = arrayListOf<String>()
        var rowTitle = 'A'
        for (i in 1..row) {
            title.add("/")
            for (j in 1..col) {
                val colTitle = j.toString()
                title.add(rowTitle + colTitle)
                seats.add(Seat(rowTitle.toString(), colTitle, auditorium!!.id))
            }
            ++rowTitle
        }
        return title
    }

    fun createSeatMap(seatMap: ArrayList<String>, seats_no: Int) {
        auditorium!!.map = seatMap
        auditorium!!.seats_no = seats_no
        val db = Firebase.firestore
        // Create seat map in auditorium
        db.collection("auditorium")
            .document(auditorium!!.id)
            .set(auditorium!!)
            .addOnSuccessListener {
                // Add data to seat doc
                val batch = db.batch()
                val seatCollectionRef = db.collection("seat")
                for (seat in seats) {
                    val newDocumentRef = seatCollectionRef.document()
                    batch.set(newDocumentRef, seat)
                }
                batch.commit()
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Lưu sơ đồ thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        val replyIntent = Intent()
                        replyIntent.putExtra("seats_no", seats_no)
                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()
                    }
                    .addOnFailureListener {e ->
                        Log.w("DB", "Error adding document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error adding document", e)
            }
    }

    fun makeExistedSeats(map: ArrayList<String>): String {
        var seats = ""
        for (item in map) {
            seats += "/"
            seats += item.map { if (it == '1') "A" else "_" }.joinToString("")
        }
        return seats
    }

    fun makeExistedTitles(map: ArrayList<String>): List<String> {
        val title = arrayListOf<String>()
        var rowTitle = 'A'
        var count = 0
        for (item in map) {
            var colTitle = 1
            count++
            title.add("/")
            for (i in 1..item.length) {
                count++
                if (item[i - 1] == '1') {
                    title.add(rowTitle + (colTitle++).toString())
                } else
                    title.add("")
            }
            ++rowTitle
        }
        return title
    }
}