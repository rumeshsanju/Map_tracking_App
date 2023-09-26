package com.example.googlemap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val b1 = findViewById<Button>(R.id.b1)
        val b2 = findViewById<Button>(R.id.b2)
        val b3 = findViewById<Button>(R.id.b3)
        val b4 = findViewById<Button>(R.id.b4)

        b1.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        b2.setOnClickListener {
            val intent = Intent(this,Address::class.java)
            startActivity(intent)
            finish()
        }
        b3.setOnClickListener {
            val intent = Intent(this,Distance::class.java)
            startActivity(intent)
            finish()
        }
        b4.setOnClickListener {
            finishAffinity()
        }
    }
}