package com.example.stawa

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreatePost : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imageUri: Uri
    private lateinit var storageReference: StorageReference
    private val PICK_IMAGE_REQUEST = 1
    private var imageUrl: String = ""
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
        val btnimagen: Button =findViewById(R.id.saveprofile4)
        btnimagen.setOnClickListener(){
            seleccionarImagenDeGaleria()
        }
        btnagregarpost.setOnClickListener(){
            val contenidoText : TextView =findViewById(R.id.contenidoa)
            val cantidadText: TextView =findViewById(R.id.cantidada)
            val contenido=contenidoText.text.toString()
            val cantidad=cantidadText.text.toString()
            if (contenido.isNotEmpty() && cantidad.isNotEmpty() && ::imageUri.isInitialized) {
                uploadImageAndCreatePost(contenido, cantidad)
            } else {
                Toast.makeText(this, "Por favor, completa todas las casillas y selecciona una imagen", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun seleccionarImagenDeGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                imageUri = selectedImageUri
                val imageView: ImageView = findViewById(R.id.imagenpost)
                imageView.setImageURI(imageUri)
            }
        }
    }
    private fun uploadImageAndCreatePost(contenido: String, cantidad: String) {
        storageReference = FirebaseStorage.getInstance().getReference("Post/" + auth.currentUser?.uid)
        val imageFileName = "${auth.currentUser?.uid}_${System.currentTimeMillis()}.jpg"
        val imageRef = storageReference.child(imageFileName)

        imageRef.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            // Obtiene la URL de la imagen cargada
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                Log.d("CreatePost", "URL de la imagen: $imageUrl")
                Toast.makeText(baseContext, "Imagen cargada correctamente", Toast.LENGTH_SHORT).show()

                val uid = auth.currentUser?.uid
                val currentUser = auth.currentUser
                val user = FirebaseAuth.getInstance().currentUser
                val name = user?.displayName
                val postID = databaseReference.push().key
                val post = Post(postID, uid, name, contenido, cantidad, imageUrl)

                if (postID != null) {
                    databaseReference.child(postID).setValue(post).addOnCompleteListener() {
                        if (it.isSuccessful) {
                            Toast.makeText(baseContext, "Publicacion creada correctamente", Toast.LENGTH_SHORT).show()
                            val i = Intent(this, PrincipalActivity::class.java)
                            startActivity(i)
                        } else {
                            Toast.makeText(
                                baseContext,
                                "Error: no se pudo crear la publicacion" + it.exception,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(baseContext, "Error: no se pudo obtener la URL de la imagen", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(baseContext, "Error: no se pudo guardar la imagen", Toast.LENGTH_SHORT).show()
        }
    }
}