package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.movieticketsonlinebooking.R
import com.google.android.material.textfield.TextInputEditText

class SignupActivity1 : AppCompatActivity() {
    var loginButton: Button? = null
    var signupButton: Button? = null
    var nameEditText: TextInputEditText? = null
    var emailEditText: TextInputEditText? = null
    var phoneEditText: TextInputEditText? = null
    var passwordEditText: TextInputEditText? = null


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

        nameEditText = findViewById(R.id.activity_signup_input_name)
        emailEditText = findViewById(R.id.activity_signup_input_email)
        phoneEditText = findViewById(R.id.activity_signup_input_phone)
        passwordEditText = findViewById(R.id.activity_signup_input_password)

        if (nameEditText?.text.toString() == "") {
            Toast.makeText(applicationContext, "You should input your name", Toast.LENGTH_SHORT).show()
            return
        }
        if (emailEditText?.text.toString() == "") {
            Toast.makeText(applicationContext, "You should input your email", Toast.LENGTH_SHORT).show()
            return
        }
        if (phoneEditText?.text.toString() == "") {
            Toast.makeText(applicationContext, "You should input your phone", Toast.LENGTH_SHORT).show()
            return
        }
        if (passwordEditText?.text.toString() == "") {
            Toast.makeText(applicationContext, "You should input your password", Toast.LENGTH_SHORT).show()
            return
        }

    }
}