package com.example.movieticketsonlinebooking.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.CurrentUser
import com.example.movieticketsonlinebooking.viewmodels.User
import com.example.movieticketsonlinebooking.viewmodels.UserManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class AccountEditProfileActivity : AppCompatActivity() {
    var emailTV: TextView? = null
    var usernameTV: TextView? = null
    var nameTV: TextView? = null
    var telTV: TextView? = null
    var dobTV: TextView? = null
    var sexRG: RadioGroup? = null
    var saveBtn: Button? = null

    val userManager: CurrentUser = UserManager.getCurrentUser()
    var user: User? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_edit_profile)
        FirebaseApp.initializeApp(this@AccountEditProfileActivity)

        emailTV = findViewById(R.id.emailET)
        usernameTV = findViewById(R.id.usernameET)
        nameTV = findViewById(R.id.nameET)
        telTV = findViewById(R.id.telET)
        dobTV = findViewById(R.id.dobET)
        sexRG = findViewById(R.id.sexRG)
        saveBtn = findViewById(R.id.saveBtn)

        val dateTextView = findViewById<ImageView>(R.id.calendar_icon)
        var cal = Calendar.getInstance()
        dobTV!!.text = "${Calendar.DAY_OF_MONTH}/${Calendar.MONTH+1}/${Calendar.YEAR}"
        dateTextView.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this@AccountEditProfileActivity,
                { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "dd/MM/yyyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    dobTV!!.setText(sdf.format(cal.time))
                }, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
        }

        coroutineScope.launch {
            user = getUserData()
            emailTV!!.text = user?.email
            usernameTV!!.text = user?.username
            nameTV!!.text = user?.name
            telTV!!.text = user?.tel
            dobTV!!.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(user?.dob)
            val radioButtonId = when (user?.sex) {
                "Nam" -> R.id.maleRadioButton
                "Nữ" -> R.id.femaleRadioButton
                "Khác" -> R.id.otherRadioButton
                else -> R.id.maleRadioButton
            }
            sexRG!!.check(radioButtonId)
        }

        saveBtn!!.setOnClickListener {
            editUser()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    suspend fun getUserData(): User? = runCatching {
        val db = Firebase.firestore
        val result = db.collection("user")
            .document(userManager.userID!!)
            .get()
            .await()
        result.toObject(User::class.java)
    }.getOrElse {
        Log.w("DB", "Error getting documents.", it)
        null
    }

    fun editUser() {
        val db = Firebase.firestore
        db.collection("user")
            .document(user!!.id).set(
                User(
                    usernameTV!!.text.toString(),
                    nameTV!!.text.toString(),
                    "",
                    findViewById<RadioButton>(sexRG!!.checkedRadioButtonId).text.toString(),
                    telTV!!.text.toString(),
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dobTV!!.text.toString()),
                    emailTV!!.text.toString(),
                )
            )
            .addOnSuccessListener {
                val replyIntent = Intent()
                replyIntent.putExtra("user", user)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("DB", "Error edit document", e)
            }
    }
}