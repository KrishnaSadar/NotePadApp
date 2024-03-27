package com.example.employeedata


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import taimoor.sultani.sweetalert2.Sweetalert


class MainActivity : AppCompatActivity() {

    private lateinit var btnInsertData: Button
    private lateinit var btnFetchData: Button
    private lateinit var logOut: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnInsertData = findViewById(R.id.btnInsertData)
        btnFetchData = findViewById(R.id.btnFetchData)
        logOut = findViewById(R.id.imageView)
        val username = intent.getStringExtra("username")
        val pass = intent.getStringExtra("pass")
        btnInsertData.setOnClickListener {
            val intent = Intent(this, InsertionActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("pass", pass)
            startActivity(intent)
        }
            logOut.setOnClickListener{
                Sweetalert(this, Sweetalert.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("You want to logOut!")
                    .setConfirmText("")
                    .setConfirmClickListener (){
                        sDialog -> sDialog.dismissWithAnimation() }
                    .setCancelButton(
                        "LogOut"
                    ) {
                        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.remove("username")
                        editor.apply()

                        // Navigate to the LoginActivity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                         }
                    .show()

            }
        btnFetchData.setOnClickListener {


            val intent = Intent(this, FetchingActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("pass", pass)
            startActivity(intent)
        }

    }
}