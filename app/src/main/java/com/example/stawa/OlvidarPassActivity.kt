package com.example.stawa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OlvidarPassActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_olvidar_pass)

        val txtcorreo:TextView = findViewById(R.id.txtemailcambio)
        val btnCambiar:Button = findViewById(R.id.btnCambiar)
        btnCambiar.setOnClickListener(){
            val email=txtcorreo.text.toString()
            if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                if(email.isEmpty()){
                    txtcorreo.error="Ingrese un correo electronico"
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() and !email.isEmpty()){
                    txtcorreo.error="El correo electronico no es válido"
                }
            }else{
                sendPasswordReset(txtcorreo.text.toString())
            }

        }
        firebaseAuth= Firebase.auth
    }

    private fun sendPasswordReset(email:String){
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(){task->
                if(task.isSuccessful){
                    Toast.makeText(baseContext,"Se ha enviado el correo de cambio de contraseña",Toast.LENGTH_SHORT).show()

                }else{
                    Toast.makeText(baseContext,"Error:Correo incorrecto",Toast.LENGTH_SHORT).show()
                }

            }
    }
}