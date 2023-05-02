package com.example.movieticketsonlinebooking.activities

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
import com.google.android.material.textfield.TextInputEditText
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
    var emailEditText: TextInputEditText? = null
    var passwordEditText: TextInputEditText? = null

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

        emailEditText = findViewById(R.id.activity_login_input_email)
        passwordEditText = findViewById(R.id.activity_login_input_password)

        loginButton = findViewById(R.id.activity_login_button_login)
        loginButton?.setOnClickListener {
            var email = emailEditText!!.text.toString()
            var password = passwordEditText!!.text.toString()
            if (email == "") {
                Toast.makeText(applicationContext, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password == "") {
                Toast.makeText(applicationContext, "Vui lòng nhập password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = FirebaseAuth.getInstance().currentUser
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
                                    newUser["username"] = ""
                                    newUser["name"] = ""
                                    newUser["email"] = email ?: ""
                                    newUser["tel"] = ""
                                    newUser["sex"] = ""
                                    newUser["hashpassword"] = ""
                                    newUser["dob"] = FieldValue.serverTimestamp()
                                    newUser["is_banned"] = false
                                    newUser["is_deleted"] = false
                                    // Add any additional fields and values you want to store
                                    userDocument.set(newUser)
                                        .addOnSuccessListener {

                                        }
                                        .addOnFailureListener { e ->

                                        }
                                }
                                UserManager.login(firebaseUser?.uid!!,email ?: "",email ?: "", 0)
                                val intent = Intent()
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
                        }
                        // The user has successfully authenticated and their UID is available
                    } else {
                        // The authentication failed, handle the error here
                        Toast.makeText(applicationContext, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show()
                    }
                }
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

                                            }
                                            .addOnFailureListener { e ->

                                            }
                                    }
                                    UserManager.login(firebaseUser?.uid!!,account?.displayName ?: "",account?.email ?: "", 1)
                                    val intent = Intent()
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                            }

                        } else {
                            // Sign-in failed, handle the exception
                            Log.e(TAG, "Sign-in failed: ${authTask.exception}")
                            Toast.makeText(applicationContext, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}