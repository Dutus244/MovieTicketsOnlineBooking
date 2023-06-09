package com.example.movieticketsonlinebooking.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.databinding.ActivityHomeBinding


class AccountFragment1 : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    var loginButton : Button ? = null
    var signupButton: Button ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_account1,
            container, false
        )

        loginButton = view.findViewById(R.id.activity_account_button_login)
        signupButton = view.findViewById(R.id.activity_account_button_signup)

        loginButton?.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivityForResult(intent, 1001)
        }

        signupButton?.setOnClickListener {
            val intent = Intent(activity, SignupActivity1::class.java)
            startActivity(intent)
        }

        return view
    }

    companion object {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            transaction.replace(R.id.frame_layout, AccountFragment2())

            transaction.commit()
        }
    }
}