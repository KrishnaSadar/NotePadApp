package com.example.employeedata

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.*
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signup: TextView
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login)
        signup = findViewById(R.id.signup)

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("users")
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Check if the user is already logged in
        if (sharedPreferences.contains("username")) {
            val username = sharedPreferences.getString("username", "error443")
            val intent=Intent(this,MainActivity::class.java)
            intent.putExtra("Employees",username)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {
            loginUser()
        }

        signup.setOnClickListener {
            val k = Intent(this@LoginActivity, SignUp::class.java)
            startActivity(k)
            finish()
        }
    }

    private fun loginUser() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            MotionToast.createToast(
                this,
                "Oops",
                "Please fill all the Fields",
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.montserrat_bold)
            )
            return
        }

        // Check if the user exists in the database
        val userRef = database.child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // User exists, check password
                    val storedPassword = snapshot.child("pass").value.toString()

                    if (password == storedPassword) {
                        // Save the username to SharedPreferences
                        sharedPreferences.edit().putString("username", username).apply()
                          val intent=Intent(this@LoginActivity,MainActivity::class.java)
                        intent.putExtra("Employees",username)
                       startActivity(intent)
                        finish()
                    } else {
                        MotionToast.createToast(
                            this@LoginActivity,
                            "Oops😒",
                            "Incorrect Password",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(
                                this@LoginActivity,
                                www.sanju.motiontoast.R.font.montserrat_bold
                            )
                        )
                    }
                } else {
                    // User does not exist
                    MotionToast.createToast(
                        this@LoginActivity,
                        "Oops🥲",
                        "Username does not Exist ",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(
                            this@LoginActivity,
                            www.sanju.motiontoast.R.font.montserrat_bold
                        )
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("Employees", username)
        startActivity(intent)
    }
}
