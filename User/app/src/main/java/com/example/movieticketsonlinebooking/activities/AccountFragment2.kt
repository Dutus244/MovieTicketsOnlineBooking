package com.example.movieticketsonlinebooking.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.User
import com.example.movieticketsonlinebooking.viewmodels.UserManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class AccountFragment2 : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var nameTextView: TextView? = null
    var emailTextView: TextView? = null
    var editProfileButton: Button? = null
    var historyButton: Button? = null
    var signoutButton: Button? = null
    var gso: GoogleSignInOptions? = null
    var gsc: GoogleSignInClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(
            R.layout.fragment_account2,
            container, false
        )

        nameTextView = view.findViewById(R.id.activity_account_textview_name)
        emailTextView = view.findViewById(R.id.activity_account_textview_email)
        editProfileButton = view.findViewById(R.id.activity_account_button_change_info)
        historyButton = view.findViewById(R.id.activity_account_button_history)

        editProfileButton!!.setOnClickListener {
            val intent = Intent(activity, AccountEditProfileActivity::class.java)
            activity?.startActivityForResult(intent, 100)
        }
        historyButton!!.setOnClickListener {
            val intent = Intent(activity, HistoryBookingActivity::class.java)
            activity?.startActivityForResult(intent, 101)
        }

        if (UserManager.getCurrentUser().typeAccount == 0) {
            nameTextView?.text = UserManager.getCurrentUser().username
            emailTextView?.text = UserManager.getCurrentUser().email
        }
        else if (UserManager.getCurrentUser().typeAccount == 1) {
            gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(getString(R.string.your_web_client_id))
                    .build()
            gsc = activity?.let { GoogleSignIn.getClient(it, gso!!) }

            val acct = activity?.let { GoogleSignIn.getLastSignedInAccount(it) }

            if (acct != null) {
                val personName = acct.displayName
                val personEmail = acct.email

                nameTextView?.text = personName
                emailTextView?.text = personEmail
            }
        }


        signoutButton = view.findViewById(R.id.activity_account_button_logout)
        signoutButton?.setOnClickListener {
            signOut()
        }
        return view
    }

    fun signOut() {
        if (UserManager.getCurrentUser().typeAccount == 0) {
            FirebaseAuth.getInstance().signOut()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            transaction.replace(R.id.frame_layout, AccountFragment1())

            transaction.commit()
        }
        else if (UserManager.getCurrentUser().typeAccount == 1) {
            gsc!!.signOut().addOnCompleteListener {

                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()

                transaction.replace(R.id.frame_layout, AccountFragment1())

                transaction.commit()
            }
        }
        UserManager.logout()
    }
    companion object {

    }
}