package com.example.stawa

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class Profile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imageUri:Uri
    private lateinit var storageReference: StorageReference
    private val PICK_IMAGE_REQUEST = 1
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth= FirebaseAuth.getInstance()
        val uid=auth.currentUser?.uid
        databaseReference= FirebaseDatabase.getInstance().getReference("users")
        val btnguardar: Button = findViewById(R.id.saveprofile)
        val nombre : TextView=findViewById(R.id.namep)
        val apellido:TextView=findViewById(R.id.apellidop)
        val bio:TextView=findViewById(R.id.biop)
        val btnimagen:Button=findViewById(R.id.saveprofile2)
        val numEdit:TextView=findViewById(R.id.numedit)


        btnimagen.setOnClickListener(){
            seleccionarImagenDeGaleria()
        }
        btnguardar.setOnClickListener(){
            val firstname = nombre.text.toString()
            val lastname = apellido.text.toString()
            val bio = bio.text.toString()
            val num = numEdit.text.toString()

            if (firstname.isNotEmpty() && lastname.isNotEmpty() && bio.isNotEmpty() && imageUri != null && num.isNotEmpty() ) {
                val user = User(firstname, lastname, bio, num)
                val currentUser: FirebaseUser? = auth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(firstname)
                    .build()
                currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (uid != null) {
                            databaseReference.child(uid).setValue(user).addOnCompleteListener() {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        baseContext,
                                        "Datos guardados correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val i = Intent(this, SignInActivity::class.java)
                                    startActivity(i)
                                } else {
                                    Toast.makeText(
                                        baseContext,
                                        "Error: no se pudieron guardar los datos" + it.exception,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Por favor, completa todos los campos y selecciona una imagen",
                    Toast.LENGTH_SHORT
                ).show()
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
                // Aquí puedes hacer algo con la imagen seleccionada, por ejemplo, mostrarla en un ImageView
                val imageView: ImageView = findViewById(R.id.imagenp)
                imageView.setImageURI(selectedImageUri)

                // Guarda la referencia a la imagen seleccionada para subirla posteriormente
                imageUri = selectedImageUri
                storageReference= FirebaseStorage.getInstance().getReference("Users/"+auth.currentUser?.uid)
                storageReference.putFile(imageUri).addOnSuccessListener {
                    Toast.makeText(baseContext,"Imagen cargada correctamente correctamente",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener(){
                    Toast.makeText(baseContext,"Error: no se pudo guardar la imagen",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




}