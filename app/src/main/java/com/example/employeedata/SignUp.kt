package com.example.employeedata

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class SignUp: AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        signUpButton = findViewById(R.id.signup)

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("users")

        signUpButton.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            // Handle empty fields
            MotionToast.createToast(this,
                "Oops",
                "Plese fill all the Fields",
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.montserrat_bold))
            return
        }

        // Check if the username is already taken
        val userRef = database.child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Username already taken
                    // Handle username already exists
                    MotionToast.createToast(this@SignUp,
                        "Oops",
                        "Username is alredy taken",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this@SignUp, www.sanju.motiontoast.R.font.montserrat_bold))
                } else {
                    // Username is available, proceed with registration
                    val user = password.toString()

                    // Save user data in the database
                    userRef.setValue(username)
                    val db=userRef.child("pass")
                    db.setValue(password)

                    // TODO: Implement your registration success logic
                    MotionToast.createToast(this@SignUp,
                        "Hurray",
                        "Registration Succesfull",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this@SignUp, www.sanju.motiontoast.R.font.montserrat_bold))
                    // You may also want to navigate to the login screen or perform other actions
                    // after successful registration
                    val intent=Intent(this@SignUp,MainActivity::class.java)
                    intent.putExtra("username",username)
                    intent.putExtra("pass",password)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
