package com.example.movieticketsonlinebooking.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.movieticketsonlinebooking.R
import com.example.movieticketsonlinebooking.viewmodels.User
import com.example.movieticketsonlinebooking.viewmodels.UserManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import java.time.LocalDate
import java.util.*

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

        nameEditText = findViewById(R.id.activity_signup_input_name)
        emailEditText = findViewById(R.id.activity_signup_input_email)
        phoneEditText = findViewById(R.id.activity_signup_input_phone)
        passwordEditText = findViewById(R.id.activity_signup_input_password)

        signupButton = findViewById(R.id.activity_signup_1_button_signup)
        signupButton?.setOnClickListener {
            var name = nameEditText?.text.toString()
            var email = emailEditText?.text.toString()
            var phone = phoneEditText?.text.toString()
            var password = passwordEditText?.text.toString()

            if (name == "") {
                Toast.makeText(applicationContext, "Bạn phải nhập tên", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email == "") {
                Toast.makeText(applicationContext, "Bạn phải nhập email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phone == "") {
                Toast.makeText(applicationContext, "Bạn phải nhập số điện thoại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password == "") {
                Toast.makeText(applicationContext, "Bạn phải nhập password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(applicationContext, "Password của bạn không được ít hơn 6 ký tự", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result
                        if (result != null && result.signInMethods != null && result.signInMethods!!.isNotEmpty()) {
                            // Email is already registered with Firebase, handle the error
                            Toast.makeText(applicationContext, "Email mà bạn dùng để đăng ký đã tồn tại", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        } else {
                            // Email is not registered with Firebase, continue with sign-up process
                        }
                    } else {
                        // Handle error
                    }
                }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
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
                                    newUser["username"] = name ?: ""
                                    newUser["name"] = name ?: ""
                                    newUser["email"] = email ?: ""
                                    newUser["tel"] = phone
                                    newUser["sex"] = ""
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
                    } else {
                        // The sign-up process failed, handle the error here
                    }
                }

        }
    }
}