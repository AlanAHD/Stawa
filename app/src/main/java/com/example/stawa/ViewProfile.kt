package com.example.stawa

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
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
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageUri:Uri
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
        val nombreTextView: TextView = findViewById(R.id.nombrev)
        val apellidoTextView: TextView = findViewById(R.id.apellidov)
        val bioTextView: TextView = findViewById(R.id.biov)
        nombreTextView.isEnabled=false
        apellidoTextView.isEnabled=false
        bioTextView.isEnabled=false

        val btncambiarimagen:Button=findViewById(R.id.Cperfil)
        val btnsalir:Button=findViewById(R.id.Sperfil)
        val btnguardar:Button=findViewById(R.id.Gperfil)
        val btnborrar:Button=findViewById(R.id.Bperfil)
        val btneditar: Button = findViewById(R.id.Eperfil)
        btncambiarimagen.setOnClickListener(){
            seleccionarImagenDeGaleria()

        }
        btnsalir.setOnClickListener(){
            nombreTextView.isEnabled=false
            apellidoTextView.isEnabled=false
            bioTextView.isEnabled=false
            btneditar.visibility= View.VISIBLE
            btnborrar.visibility= View.VISIBLE
            btnguardar.visibility=View.GONE
            btnsalir.visibility=View.GONE
            btncambiarimagen.visibility=View.GONE
        }
        btneditar.setOnClickListener(){
            nombreTextView.isEnabled=true
            apellidoTextView.isEnabled=true
            bioTextView.isEnabled=true
            btneditar.visibility= View.GONE
            btnborrar.visibility= View.GONE
            btnguardar.visibility=View.VISIBLE
            btnsalir.visibility=View.VISIBLE
            btncambiarimagen.visibility=View.VISIBLE

        }
        btnguardar.setOnClickListener(){
            val firstname = nombreTextView.text.toString()
            val lastname = apellidoTextView.text.toString()
            val bio = bioTextView.text.toString()

            if (firstname.isNotEmpty() && lastname.isNotEmpty() && bio.isNotEmpty()) {
                val user = User(firstname, lastname, bio)
                val currentUser: FirebaseUser? = auth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(firstname)
                    .build()
                currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (uid != null) {
                            val userRef =
                                FirebaseDatabase.getInstance().getReference("users").child(uid)
                            userRef.setValue(user).addOnCompleteListener() {
                                if (it.isSuccessful) {
                                    val postsReference =
                                        FirebaseDatabase.getInstance().getReference("posts")
                                    val query = postsReference.orderByChild("userId").equalTo(uid)
                                    query.addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            // Actualizar el nombre de usuario en cada publicación
                                            for (postSnapshot in dataSnapshot.children) {
                                                val postKey = postSnapshot.key
                                                val post =
                                                    postSnapshot.getValue(Post::class.java)
                                                if (post?.userId == uid) {
                                                    post.username = firstname // Actualizar el nombre de usuario
                                                    postSnapshot.ref.setValue(post) // Actualizar la publicación en la base de datos
                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // Ocurrió un error al obtener las publicaciones del usuario
                                            // Puedes mostrar un mensaje de error o realizar alguna acción adicional
                                        }
                                    })
                                    Toast.makeText(
                                        baseContext,
                                        "Datos guardados correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Deshabilitar la edición nuevamente
                                    nombreTextView.isEnabled = false
                                    apellidoTextView.isEnabled = false
                                    bioTextView.isEnabled = false

                                    // Mostrar los botones apropiados
                                    btneditar.visibility = View.VISIBLE
                                    btnborrar.visibility = View.VISIBLE
                                    btnguardar.visibility = View.GONE
                                    btnsalir.visibility = View.GONE
                                    btncambiarimagen.visibility = View.GONE

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
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
        btnborrar.setOnClickListener(){
            val user = FirebaseAuth.getInstance().currentUser
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
            val storageReference = FirebaseStorage.getInstance().getReference("Users/$uid")

            // Eliminar datos del perfil en la base de datos
            userRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Eliminar Post
                    val postReference=FirebaseDatabase.getInstance().getReference("posts")
                    val query=postReference.orderByChild("userId").equalTo(uid)
                    query.addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (postSnapshot in dataSnapshot.children) {
                                // Eliminar la publicación
                                postSnapshot.ref.removeValue()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Ocurrió un error al obtener las publicaciones del usuario
                            // Puedes mostrar un mensaje de error o realizar alguna acción adicional
                        }
                    })
                    // Eliminar la imagen de Storage
                    storageReference.delete().addOnSuccessListener {
                        // Eliminar la cuenta de usuario
                        user?.delete()?.addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                // Eliminación exitosa, redirigir a la pantalla de inicio de sesión
                                Toast.makeText(baseContext, "Perfil eliminado correctamente", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, Inicio::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // Error al eliminar la cuenta de usuario
                                Toast.makeText(baseContext, "Error al eliminar el perfil", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener {
                        // Error al eliminar la imagen de Storage
                        Toast.makeText(baseContext, "Error al eliminar la imagen del perfil", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Error al eliminar los datos del perfil en la base de datos
                    Toast.makeText(baseContext, "Error al eliminar los datos del perfil", Toast.LENGTH_SHORT).show()
                }
            }
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
                val imageView: ImageView = findViewById(R.id.imagenv)
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