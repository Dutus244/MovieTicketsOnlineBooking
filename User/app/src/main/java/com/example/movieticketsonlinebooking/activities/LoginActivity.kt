package com.example.movieticketsonlinebooking.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.movieticketsonlinebooking.R

class LoginActivity : AppCompatActivity() {
    var signupButton: Button? = null
    var loginButton: Button? = null
    var forgotPasswordButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signupButton = findViewById(R.id.activity_login_button_signup)
        signupButton?.setOnClickListener {
            val intent = Intent(applicationContext, SignupActivity1::class.java)
            startActivity(intent)
        }

        loginButton = findViewById(R.id.activity_login_button_login)
        loginButton?.setOnClickListener {
            // Luu thong tin dang nhap cua nguoi dung tren ung dung
            val sharedPreferences = getSharedPreferences("TumLumCinemas", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("email", "duynguyen24th@gmail.com")
            editor.apply()


            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordButton = findViewById(R.id.activity_login_button_forgot_password)
        forgotPasswordButton?.setOnClickListener {
            val intent = Intent(applicationContext, ForgotPasswordActivity1::class.java)
            startActivity(intent)
        }
    }
}