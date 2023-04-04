package com.example.admin.auditoriums

import android.R.attr.maxLength
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.RequestCode
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class AuditoriumList : AppCompatActivity() {
    var autoCompleteTextView: AutoCompleteTextView? = null
    var audiRecyclerView: RecyclerView? = null
    var addBtn: Button? = null

    var auditoriums: List<Auditorium> = listOf()
    var cinema_id: String? = null

    // Create a CoroutineScope instance in the activity
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auditorium_list)

        FirebaseApp.initializeApp(this)
        val intent = intent
        cinema_id = intent.getStringExtra("cinema_id")

        autoCompleteTextView = findViewById(R.id.audiListACTV)
        audiRecyclerView = findViewById(R.id.audiListRecyclerView)
        addBtn = findViewById(R.id.audiListAddBtn)

        val adapter = AuditoriumListAdapter(this, auditoriums)
        audiRecyclerView!!.adapter = adapter
        audiRecyclerView!!.layoutManager = LinearLayoutManager(this)

        addBtn!!.setOnClickListener {
            val dialog: AlertDialog = createaAuditorimDialog(cinema_id!!)
            dialog.show()
        }

        coroutineScope.launch {
            auditoriums = getAuditoriumData()
            audiRecyclerView!!.adapter = AuditoriumListAdapter(this@AuditoriumList, auditoriums)

            val autoCompleteAdapter =
                ArrayAdapter(
                    this@AuditoriumList,
                    android.R.layout.simple_list_item_single_choice,
                    auditoriums.map { it.name })
            autoCompleteTextView!!.setAdapter(autoCompleteAdapter)
            autoCompleteTextView!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    audiRecyclerView!!.adapter =
                        AuditoriumListAdapter(this@AuditoriumList, auditoriums.filter {
                            it.name.contains(autoCompleteTextView!!.text, true)
                        })
                }
            })
        }
    }

    // Cancel the coroutine scope when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    suspend fun getAuditoriumData(): List<Auditorium> = runCatching {
        val db = Firebase.firestore
        val result = db.collection("auditorium")
            .whereEqualTo("cinema_id", cinema_id)
            .whereEqualTo("is_deleted", false)
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .await()
        result.toObjects(Auditorium::class.java)
    }
        .getOrElse {
            Log.w("DB", "Error getting documents.", it)
            emptyList()
        }

    fun createAuditorium(name: String, cinema_id: String) {
        val db = Firebase.firestore
        db.collection("auditorium")
            .add(Auditorium(name, cinema_id = cinema_id))
            .addOnSuccessListener {
                db.collection("cinema")
                    .document(cinema_id)
                    .update("auditoriums_no", FieldValue.increment(1))
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Thêm thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        recreate()
                    }
                    .addOnFailureListener { e ->
                        Log.w("DB", "Error adding document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error adding document", e)
            }
    }

    fun createaAuditorimDialog(cinema_id: String): AlertDialog {
        val builder = AlertDialog.Builder(this@AuditoriumList)
        val input = EditText(this@AuditoriumList)
        input.setSingleLine()
        input.filters = arrayOf<InputFilter>(LengthFilter(25))
        builder.setView(input)
        builder.setMessage("Nhập tên phòng chiếu")
            .setPositiveButton("Thêm") { _, _ ->
                var name = input.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    createAuditorium(name, cinema_id)
                }
            }
            .setNegativeButton("Hủy") { _, _ ->

            }
        return builder.create()
    }

    fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode.AUDITORIUM_SCREEN_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                recreate()
            }
        }
    }
}