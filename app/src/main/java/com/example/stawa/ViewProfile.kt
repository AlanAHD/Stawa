package com.example.stawa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ViewProfile : AppCompatActivity() {
    private lateinit var nombre:TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var auth: FirebaseAuth
    private lateinit var DatabaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)
        auth= FirebaseAuth.getInstance()
        val uid=auth.currentUser?.uid?:""
        firebaseAuth= Firebase.auth
        nombre=findViewById(R.id.idbienvenida)
        val user= FirebaseAuth.getInstance().currentUser
        val name=user?.displayName

        if(name != null){
            nombre.text=name
        }

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Obtén los valores de los datos del usuario
                    val firstname = dataSnapshot.child("firstname").getValue(String::class.java)
                    val lastname = dataSnapshot.child("lastname").getValue(String::class.java)
                    val bio = dataSnapshot.child("bio").getValue(String::class.java)

                    // Muestra los datos en TextViews u otros componentes
                    val nombreTextView: TextView = findViewById(R.id.nombrev)
                    val apellidoTextView: TextView = findViewById(R.id.apellidov)
                    val bioTextView: TextView = findViewById(R.id.biov)

                    nombreTextView.text = firstname
                    apellidoTextView.text = lastname
                    bioTextView.text = bio
                    //Cargar imagen
                    cargarImagen(uid)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Ocurrió un error al obtener los datos del usuario
                // Puedes mostrar un mensaje de error o realizar alguna acción adicional
            }
        })

    }

    private fun cargarImagen(uid: String) {
        storageReference = FirebaseStorage.getInstance().getReference("Users/$uid")

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()

            // Cargar la imagen en el ImageView utilizando Picasso
            val imageView: ImageView = findViewById(R.id.imagenv)
            Picasso.get().load(imageUrl).into(imageView)

        }.addOnFailureListener {
            Toast.makeText(baseContext,"Error:No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
            // Ocurrió un error al descargar la imagen de Firebase Storage
            // Puedes mostrar un mensaje de error o realizar alguna acción adicional
        }

    }
}