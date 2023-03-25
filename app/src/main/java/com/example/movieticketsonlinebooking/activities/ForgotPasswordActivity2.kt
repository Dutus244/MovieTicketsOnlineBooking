package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.fragments.GenericTextWatcher_ForgotPassword

class ForgotPasswordActivity2 : AppCompatActivity() {
    var otp_textbox_one: EditText? = null
    var otp_textbox_two:EditText? = null
    var otp_textbox_three:EditText? = null
    var otp_textbox_four:EditText? = null

    var forgotPasswordButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password2)

        otp_textbox_one = findViewById(R.id.activity_forgot_password_2_otp_edit_box1)
        otp_textbox_two = findViewById(R.id.activity_forgot_password_2_otp_edit_box2)
        otp_textbox_three = findViewById(R.id.activity_forgot_password_2_otp_edit_box3)
        otp_textbox_four = findViewById(R.id.activity_forgot_password_2_otp_edit_box4)

        var edit = arrayOf<EditText>(otp_textbox_one!!, otp_textbox_two!!, otp_textbox_three!!, otp_textbox_four!!)

        otp_textbox_one?.addTextChangedListener(
            GenericTextWatcher_ForgotPassword(
                otp_textbox_one,
                edit
            )
        )
        otp_textbox_two?.addTextChangedListener(
            GenericTextWatcher_ForgotPassword(
                otp_textbox_two,
                edit
            )
        )
        otp_textbox_three?.addTextChangedListener(
            GenericTextWatcher_ForgotPassword(
                otp_textbox_three,
                edit
            )
        )
        otp_textbox_four?.addTextChangedListener(
            GenericTextWatcher_ForgotPassword(
                otp_textbox_four,
                edit
            )
        )

        forgotPasswordButton = findViewById(R.id.activity_forgot_password_2_button_forgot_password)
        forgotPasswordButton?.setOnClickListener {
            val intent = Intent(applicationContext, ForgotPasswordActivity3::class.java)
            startActivity(intent)
        }
    }
}