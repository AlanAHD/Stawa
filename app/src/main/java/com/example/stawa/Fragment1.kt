package com.example.stawa

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stawa.databinding.Fragment1Binding
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class Fragment1 : Fragment() {
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
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    private var _binding: Fragment1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = Fragment1Binding.inflate(inflater, container, false)
        firebaseAuth=Firebase.auth
        database = FirebaseDatabase.getInstance()
        postsReference =FirebaseDatabase.getInstance().getReference("posts")
        currentUser=FirebaseAuth.getInstance().currentUser!!

        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())

        postAdapter= PostAdapter(requireContext() as Activity, emptyList(),currentUser)
        binding.recyclerView.adapter=postAdapter





        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {


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


            // Aquí puedes realizar la lógica para actualizar los datos del RecyclerView
            // por ejemplo, volver a cargar los datos desde Firebase y notificar al adaptador
            // cuando se complete la actualización de los datos.

            // Luego, al finalizar la actualización, debes detener el indicador de
            // carga de SwipeRefreshLayout llamando a setRefreshing(false):
            swipeRefreshLayout.isRefreshing = false
        }

        binding.agregarp.setOnClickListener(){
            val i=Intent(requireContext(),CreatePost::class.java)
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
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.cerrarsesion->{
                signOut()
            }
            R.id.verperfil->{
                val i=Intent(requireContext(),ViewProfile::class.java)
                startActivityForResult(i,EDIT_PROFILE_REQUEST_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun onBackPressed() {
        return
    }
    private fun signOut(){
        firebaseAuth.signOut()
        Toast.makeText(requireContext(),"Sesion cerrada correctamente",Toast.LENGTH_SHORT).show()
        val i=Intent(requireContext(),MainActivity::class.java)
        startActivity(i)
    }


}












