package com.example.stawa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class SignUpActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val txtnombre_nuevo : TextView = findViewById(R.id.edtNombre)
        val txtcorreo_nuevo : TextView = findViewById(R.id.edtEmail)
        val txtpassword1: TextView = findViewById(R.id.edtContra)
        val txtpassword2: TextView = findViewById(R.id.edtConfir)
        val btncrear: Button = findViewById(R.id.CrearCuenta)
        btncrear.setOnClickListener(){
            var correo= txtcorreo_nuevo.text.toString()
            var nombre=txtnombre_nuevo.text.toString()
            var pass1= txtpassword1.text.toString()
            var pass2= txtpassword2.text.toString()
            if( nombre.isEmpty() || correo.isEmpty() || pass1.isEmpty() || pass2.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                if (nombre.isEmpty()){
                    txtnombre_nuevo.error="Ingrese un nombre"
                }
                if(correo.isEmpty()){
                    txtcorreo_nuevo.error="Ingrese un correo"
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches() and !correo.isEmpty()){
                    txtcorreo_nuevo.error="El correo electronico no es valido"
                }
                if(pass1.isEmpty()){
                    txtpassword1.error="Ingrese la contraseña"
                }
                if (pass2.isEmpty()){
                    txtpassword2.error="Ingrese la confirmacion de la contraseña"
                }

            }else if(pass1.length <6){
                txtpassword1.error="La contraseña debe ser de 6 o mas caracteres"
            }else if (pass2.length<6){
                txtpassword2.error="La contraseña debe ser de 6 o mas caracteres"
            }
            else if(pass1.equals(pass2)){
                createAccount(txtnombre_nuevo.text.toString(),txtcorreo_nuevo.text.toString(),txtpassword1.text.toString())
            }else{
                Toast.makeText(baseContext,"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show()
                txtpassword1.requestFocus()
            }
        }

        firebaseAuth= Firebase.auth
    }

    private fun createAccount(name:String,email: String, password:String){
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    val user= firebaseAuth.currentUser
                    val profileUpdates=UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener{task->
                            if(task.isSuccessful){
                                SendEmailVerification()
                                Toast.makeText(baseContext,"Cuenta Creada Correctamente",Toast.LENGTH_SHORT).show()
                                val i = Intent(this,SignInActivity::class.java)
                                startActivity(i)
                            }
                        }
                }else{
                    val errorMessage=task.exception?.message
                    if(errorMessage !=null && errorMessage.contains("already in use")){
                        Toast.makeText(baseContext, "El correo ya está registrado.", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(baseContext,"No se pudo Crear la Cuenta, Error: "+task.exception,Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }
    private fun SendEmailVerification(){
        val user= firebaseAuth.currentUser!!
        user.sendEmailVerification().addOnCompleteListener(this){task->
            if(task.isSuccessful){

            }
        }
    }
}