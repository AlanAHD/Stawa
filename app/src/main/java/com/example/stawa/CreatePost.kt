package com.example.stawa

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference

class CreatePost : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imageUri: Uri
    private lateinit var storageReference: StorageReference
    private val PICK_IMAGE_REQUEST = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        auth= FirebaseAuth.getInstance()
        databaseReference= FirebaseDatabase.getInstance().getReference("posts")
        val uid=auth.currentUser?.uid
        val currentUser=auth.currentUser
        val user= FirebaseAuth.getInstance().currentUser
        val name=user?.displayName
        val btnagregarpost: Button = findViewById(R.id.btnposta)
        val contenido : TextView =findViewById(R.id.contenidoa)
        val cantidad: TextView =findViewById(R.id.cantidada)
        //val btnimagen: Button =findViewById(R.id.imagenpost)
        btnagregarpost.setOnClickListener(){
            val contenido=contenido.text.toString()
            val cantidad=cantidad.text.toString()
            val postID=databaseReference.push().key
            val post=Post(postID,uid,name,contenido,cantidad)
            if(postID!=null){
                databaseReference.child(postID).setValue(post).addOnCompleteListener(){
                    if(it.isSuccessful){
                        Toast.makeText(baseContext,"Publicacion creada correctamente", Toast.LENGTH_SHORT).show()
                        val i= Intent(this,PrincipalActivity::class.java)
                        startActivity(i)

                    }else{
                        Toast.makeText(baseContext,"Error: no se pudo crear la publicacion"+it.exception,
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}