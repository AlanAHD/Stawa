package com.example.stawa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class SignInActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val btningresar: Button= findViewById(R.id.idinicio)
        val txtemail: TextView = findViewById(R.id.idcorreo)
        val txtcontra: TextView=findViewById(R.id.idcontra)
        val btnRecordar: Button =findViewById(R.id.olvidar)
        firebaseAuth=Firebase.auth
        btningresar.setOnClickListener()
        {
            val email= txtemail.text.toString()
            val contra=txtcontra.text.toString()
            if (email.isEmpty() || contra.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                if(email.isEmpty()){
                    txtemail.error="Ingresa un correo electronico"
                }
                if(contra.isEmpty()){
                    txtcontra.error="Ingresa una contraseña"
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() and !email.isEmpty()){
                    txtemail.error="El correo electronico no es válido"

                }
            }else if(contra.length <6){
                txtcontra.error="La contraseña debe tener 6 o mas caracteres"
            }else{
                SignIn(txtemail.text.toString(),txtcontra.text.toString())
            }
        }

        btnRecordar.setOnClickListener(){
            val i = Intent(this,OlvidarPassActivity::class.java)
            startActivity(i)
        }

    }
    private fun SignIn(email:String, password:String){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
            Task->
            if(Task.isSuccessful){
                val user = firebaseAuth.currentUser
                val verificar= user?.isEmailVerified
                if(verificar==true){
                    Toast.makeText(baseContext,"Autentificacion exitosa",Toast.LENGTH_SHORT).show()
                    val i = Intent(this,Profile::class.java)
                    startActivity(i)
                }else{
                    Toast.makeText(baseContext,"Debes verificar tu correo electronico",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(baseContext,"Error Correo o contraseña incorrectos",Toast.LENGTH_SHORT).show()
            }
        }
    }
}