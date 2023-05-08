package com.example.stawa

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.stawa.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn: Button= findViewById(R.id.Inisesion)
        val btn1: Button = findViewById(R.id.Registrarse)
        btn1.setOnClickListener(){
            val intent:Intent= Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
        btn.setOnClickListener(){
            val intent:Intent =Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

    }

}