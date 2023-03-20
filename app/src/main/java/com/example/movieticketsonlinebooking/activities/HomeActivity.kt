package com.example.movieticketsonlinebooking.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.movieticketsonlinebooking.R
import androidx.fragment.app.Fragment
import com.example.movieticketsonlinebooking.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.activity_home_page_bottom_nevigation_home -> replaceFragment(HomeFragment())
                R.id.activity_home_page_bottom_nevigation_cinema -> replaceFragment(CinemaFragment())
                R.id.activity_home_page_bottom_nevigation_account -> replaceFragment(AccountFragment())
                else ->{

                }
            }
            true
        }
    }
}