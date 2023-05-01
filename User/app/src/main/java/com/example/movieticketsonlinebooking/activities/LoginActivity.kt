package com.example.movieticketsonlinebooking.activities

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.UserManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {
    var signupButton: Button? = null
    var loginButton: Button? = null
    var forgotPasswordButton: Button? = null
    var googleSigninButton: SignInButton? = null
    var gso: GoogleSignInOptions? = null
    var gsc: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.your_web_client_id))
                .build()
        gsc = GoogleSignIn.getClient(this, gso!!)

        signupButton = findViewById(R.id.activity_login_button_signup)
        signupButton?.setOnClickListener {
            val intent = Intent(applicationContext, SignupActivity1::class.java)
            startActivity(intent)
        }

        loginButton = findViewById(R.id.activity_login_button_login)
        loginButton?.setOnClickListener {
            // Luu thong tin dang nhap cua nguoi dung tren ung dung
//            val sharedPreferences = getSharedPreferences("TumLumCinemas", Context.MODE_PRIVATE)
//            val editor = sharedPreferences.edit()
//            editor.putString("email", "duynguyen24th@gmail.com")
//            editor.apply()


            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordButton = findViewById(R.id.activity_login_button_forgot_password)
        forgotPasswordButton?.setOnClickListener {
            val intent = Intent(applicationContext, ForgotPasswordActivity1::class.java)
            startActivity(intent)
        }

        googleSigninButton = findViewById(R.id.google_signin_button)
        googleSigninButton?.setOnClickListener {
            signIn()
        }
    }

    fun signIn() {
        val signInIntent: Intent = gsc!!.getSignInIntent()
        startActivityForResult(signInIntent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // Use the signed-in account
                val idToken = account?.idToken // Get the ID token for Firebase Authentication
                val authCredential = GoogleAuthProvider.getCredential(idToken, null) // Create an auth credential from the ID token
                FirebaseAuth.getInstance().signInWithCredential(authCredential)
                    .addOnCompleteListener(this) { authTask ->
                        if (authTask.isSuccessful) {
                            // Sign-in success, update UI or do further processing
                            val firebaseUser = FirebaseAuth.getInstance().currentUser

                            // Check if user document exists in Firestore
                            val firestore = FirebaseFirestore.getInstance()
                            val usersCollection = firestore.collection("user")
                            val userDocument = usersCollection.document(firebaseUser?.uid!!)
                            userDocument.get().addOnCompleteListener { userDocumentTask ->
                                if (userDocumentTask.isSuccessful) {
                                    val document = userDocumentTask.result
                                    if (document.exists()) {

                                    } else {
                                        // User document does not exist, create it
                                        val newUser = HashMap<String, Any>()
                                        newUser["username"] = account?.displayName ?: ""
                                        newUser["name"] = account?.familyName + " " + account?.givenName ?: ""
                                        newUser["email"] = account?.email ?: ""
                                        newUser["tel"] = ""
                                        newUser["sex"] = ""
                                        newUser["hashpassword"] = ""
                                        newUser["dob"] = FieldValue.serverTimestamp()
                                        newUser["is_banned"] = false
                                        newUser["is_deleted"] = false
                                        // Add any additional fields and values you want to store
                                        userDocument.set(newUser)
                                            .addOnSuccessListener {
                                                Log.d(TAG, "User document created successfully")
                                                // Do further processing or update UI
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e(TAG, "Error creating user document: ${e.message}")
                                                // Handle error
                                            }
                                    }
                                    UserManager.login(firebaseUser?.uid!!,account?.displayName ?: "",account?.email ?: "")
                                    val intent = Intent()
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                } else {
                                    Log.e(TAG, "Error getting user document: ${userDocumentTask.exception?.message}")
                                    // Handle error
                                }
                            }

                        } else {
                            // Sign-in failed, handle the exception
                            Log.e(TAG, "Sign-in failed: ${authTask.exception}")
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}