package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.movieticketsonlinebooking.R

class ForgotPasswordActivity3 : AppCompatActivity() {
    var forgotPasswordButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password3)

        forgotPasswordButton = findViewById(R.id.activity_forgot_password_3_button_forgot_password)
        forgotPasswordButton?.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}