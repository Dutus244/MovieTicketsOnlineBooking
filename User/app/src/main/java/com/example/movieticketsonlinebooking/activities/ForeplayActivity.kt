package com.example.movieticketsonlinebooking.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.UserManager

class ForeplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foreplay)

        // Neu nguoi dung da dang nhap thi duy tri trang thai dang nhap
        val sharedPreferences = getSharedPreferences("TumLumCinemas", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")
//        if (email != "") {
//            UserManager.login("", email!!)
//        }

        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
            finish()

        }, 3000)
    }
}