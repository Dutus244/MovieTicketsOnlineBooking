package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.movieticketsonlinebooking.R

class ForgotPasswordActivity1 : AppCompatActivity() {
    var loginButton: Button? = null
    var forgotPasswordButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password1)

        loginButton = findViewById(R.id.activity_forgot_password_1_button_login)
        loginButton?.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordButton = findViewById(R.id.activity_forgot_password_1_button_forgot_password)
        forgotPasswordButton?.setOnClickListener {
            val intent = Intent(applicationContext, ForgotPasswordActivity2::class.java)
            startActivity(intent)
        }
    }
}