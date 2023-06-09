package com.example.movieticketsonlinebooking.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.fragments.GenericTextWatcher_ForgotPassword
import com.example.movieticketsonlinebooking.fragments.GenericTextWatcher_Signup
import com.example.movieticketsonlinebooking.viewmodels.Movie
import com.example.movieticketsonlinebooking.viewmodels.User
import java.util.*

class SignupActivity2 : AppCompatActivity() {
    var otp_textbox_one: EditText? = null
    var otp_textbox_two: EditText? = null
    var otp_textbox_three: EditText? = null
    var otp_textbox_four: EditText? = null

    var signupButton: Button? = null
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup2)

        intent = intent
        user = intent.getSerializableExtra("user") as User

        val random = Random()
        val otp = random.nextInt(9000) + 1000

        otp_textbox_one = findViewById(R.id.activity_signup_2_otp_edit_box1)
        otp_textbox_two = findViewById(R.id.activity_signup_2_otp_edit_box2)
        otp_textbox_three = findViewById(R.id.activity_signup_2_otp_edit_box3)
        otp_textbox_four = findViewById(R.id.activity_signup_2_otp_edit_box4)

        var edit = arrayOf<EditText>(otp_textbox_one!!, otp_textbox_two!!, otp_textbox_three!!, otp_textbox_four!!)

        otp_textbox_one?.addTextChangedListener(
            GenericTextWatcher_Signup(
                otp_textbox_one,
                edit
            )
        )
        otp_textbox_two?.addTextChangedListener(
            GenericTextWatcher_Signup(
                otp_textbox_two,
                edit
            )
        )
        otp_textbox_three?.addTextChangedListener(
            GenericTextWatcher_Signup(
                otp_textbox_three,
                edit
            )
        )
        otp_textbox_four?.addTextChangedListener(
            GenericTextWatcher_Signup(
                otp_textbox_four,
                edit
            )
        )

        signupButton = findViewById(R.id.activity_signup_2_button_signup)
        signupButton?.setOnClickListener {
            // Luu thong tin dang nhap cua nguoi dung tren ung dung
            val sharedPreferences = getSharedPreferences("TumLumCinemas", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("email", "duynguyen24th@gmail.com")
            editor.apply()

            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}