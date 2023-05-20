package com.example.stawa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference

class ViewPost : AppCompatActivity() {
    private lateinit var nombre: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var auth: FirebaseAuth
    private lateinit var DatabaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)
        auth= FirebaseAuth.getInstance()
        val uid=auth.currentUser?.uid?:""
        firebaseAuth= Firebase.auth
        val contenidoTextView: TextView = findViewById(R.id.contenidov)
        val cantidadTextView: TextView = findViewById(R.id.cantidadv)
        val btnGuardar:Button=findViewById(R.id.SaveEditPost)
        DatabaseReference= FirebaseDatabase.getInstance().getReference("posts")
        val postId=intent.getStringExtra("postKey")?:""
        val userRef = FirebaseDatabase.getInstance().getReference("posts").child(postId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Obtén los valores de los datos del usuario
                    val nombre = dataSnapshot.child("username").getValue(String::class.java)
                    val contenido = dataSnapshot.child("contenido").getValue(String::class.java)
                    val cantidad = dataSnapshot.child("cantidad").getValue(String::class.java)

                    // Muestra los datos en TextViews u otros componentes
                    val nombreTextView: TextView = findViewById(R.id.NombreViewP)
                    val contenidoTextView: TextView = findViewById(R.id.contenidov)
                    val cantidadTextView: TextView = findViewById(R.id.cantidadv)

                    nombreTextView.text = nombre
                    contenidoTextView.text = contenido
                    cantidadTextView.text = cantidad


                    btnGuardar.setOnClickListener {
                        val contenidoEditado = contenidoTextView.text.toString()
                        val cantidadEditada = cantidadTextView.text.toString()

                        val postUpdates = mapOf<String, Any>(
                            "contenido" to contenidoEditado,
                            "cantidad" to cantidadEditada
                        )

                        userRef.updateChildren(postUpdates)
                            .addOnSuccessListener {
                                Toast.makeText(baseContext,"Datos guardados correctamente",Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(baseContext,"Datos guardados correctamente"+exception,Toast.LENGTH_SHORT).show()
                            }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Ocurrió un error al obtener los datos del usuario
                // Puedes mostrar un mensaje de error o realizar alguna acción adicional
            }
        })
    }
}