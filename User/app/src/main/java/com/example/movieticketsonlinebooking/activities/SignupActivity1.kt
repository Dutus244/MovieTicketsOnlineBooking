package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.movieticketsonlinebooking.R

class SignupActivity1 : AppCompatActivity() {
    var loginButton: Button? = null
    var signupButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup1)

        loginButton = findViewById(R.id.activity_signup_1_button_login)
        loginButton?.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        signupButton = findViewById(R.id.activity_signup_1_button_signup)
        signupButton?.setOnClickListener {
            val intent = Intent(applicationContext, SignupActivity2::class.java)
            startActivity(intent)
        }
    }
}