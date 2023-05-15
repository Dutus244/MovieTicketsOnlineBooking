package com.example.movieticketsonlinebooking.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.movieticketsonlinebooking.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity1 : AppCompatActivity() {
    var loginButton: Button? = null
    var forgotPasswordButton: Button? = null
    var emailEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password1)

        loginButton = findViewById(R.id.activity_forgot_password_1_button_login)
        loginButton?.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        emailEditText = findViewById(R.id.activity_forgot_password_1_input_email)

        forgotPasswordButton = findViewById(R.id.activity_forgot_password_1_button_forgot_password)
        forgotPasswordButton?.setOnClickListener {
            var email = emailEditText!!.text.toString()
            if (email == "") {
                Toast.makeText(applicationContext, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result
                        if (result != null && result.signInMethods != null && result.signInMethods!!.isNotEmpty()) {
                            // Email is already registered with Firebase, handle the error

                        } else {
                            // Email is not registered with Firebase, continue with sign-up process
                            Toast.makeText(applicationContext, "Email chưa được đăng ký", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }
                    } else {
                        // Handle error
                    }
                }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password reset email sent successfully
                        Toast.makeText(applicationContext, "Đã gửi email đổi mật khẩu", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // Handle error
                    }
                }
        }
    }
}