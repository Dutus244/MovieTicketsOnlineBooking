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
import android.widget.ProgressBar
import android.widget.TextView
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.User
import com.example.movieticketsonlinebooking.viewmodels.UserManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat
import java.text.NumberFormat

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
    var priceTextView: TextView? = null
    var progressBar: ProgressBar? = null

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
        priceTextView = view.findViewById(R.id.textView3)
        progressBar = view.findViewById(R.id.progressbar)


        val userId = UserManager.getCurrentUser().userID
        var totalSum = 0.0
        val db = Firebase.firestore
        val reservations = db.collection("reservation")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (reservation in querySnapshot) {
                    // Process each reservation here
                    // You can access reservation data using reservation.data

                    // For example, if the reservation has a "sum" field, you can accumulate the total sum
                    val sum = reservation.getDouble("total_price")
                    if (sum != null) {
                        totalSum += sum
                    }
                }

                // After processing all reservations, you can perform any necessary actions with the total sum
                // For example, you could display it or use it in further calculations
                priceTextView!!.text = toVND(totalSum.toInt())
                val progress = totalSum / 2000000 * 100

                progressBar!!.progress = progress.toInt()
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the query

            }



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

    private fun toVND(num: Int): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return formatter.format(num) + "đ"
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