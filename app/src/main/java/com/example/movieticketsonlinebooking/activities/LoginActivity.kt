package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.movieticketsonlinebooking.R

class LoginActivity : AppCompatActivity() {
    var signupButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signupButton = findViewById(R.id.activity_login_button_signup)
        signupButton?.setOnClickListener {
            val intent = Intent(applicationContext, SignupActivity1::class.java)
            startActivity(intent)
        }
    }
}