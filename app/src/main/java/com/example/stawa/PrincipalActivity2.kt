package com.example.stawa

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.*

class PrincipalActivity2 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imageUri: Uri
    private lateinit var postsRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var currentUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var postAdapter: PostAdapter
    private lateinit var postsReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private val EDIT_PROFILE_REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 1
    private val postsList: ArrayList<Post> = ArrayList()
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal2)


        // Listener para mostrar el nombre de la pantalla en el título de la actividad
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = destination.label
        }

        firebaseAuth=Firebase.auth
        database = FirebaseDatabase.getInstance()
        postsReference =FirebaseDatabase.getInstance().getReference("posts")
        currentUser=FirebaseAuth.getInstance().currentUser!!
        val recycle=findViewById<RecyclerView>(R.id.recyclerView)
        recycle.layoutManager=LinearLayoutManager(this)

        postAdapter= PostAdapter(this, emptyList(),currentUser)
        recycle.adapter=postAdapter

        val btncrearpost:FloatingActionButton=findViewById(R.id.agregarp)

        btncrearpost.setOnClickListener(){
            val i=Intent(this,CreatePost::class.java)
            startActivity(i)
        }

        postsReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts= mutableListOf<Post>()
                for(postSnapshop in snapshot.children){
                    val post=postSnapshop.getValue(Post::class.java)
                    post?.let{
                        posts.add(it)
                    }
                }
                postAdapter.setDataset(posts)
                postAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }








    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.cerrarsesion->{
                signOut()
            }
            R.id.verperfil->{
                val i=Intent(this,ViewProfile::class.java)
                startActivityForResult(i,EDIT_PROFILE_REQUEST_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        return
    }
    private fun signOut(){
        firebaseAuth.signOut()
        Toast.makeText(baseContext,"Sesion cerrada correctamente",Toast.LENGTH_SHORT).show()
        val i=Intent(this,MainActivity::class.java)
        startActivity(i)
    }
}