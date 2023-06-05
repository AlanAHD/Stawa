package com.example.stawa

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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

class PrincipalActivity : AppCompatActivity() {
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



    private lateinit var bottomNavigationView: BottomNavigationView

    private val fragment1 = Fragment1()
    private val fragment2 = Fragment2()
    private val fragment3 = Fragment3()
    private val fragment4 = Fragment4()
    private val fragmentManager: FragmentManager = supportFragmentManager
    private var activeFragment: Fragment = fragment1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        firebaseAuth=Firebase.auth
        val selectorColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(Color.parseColor("#EF802F"), Color.parseColor("#FFCC57"))
        )

        bottomNavigationView.itemIconTintList = selectorColors
        bottomNavigationView.itemTextColor = selectorColors


        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeitem -> {
                    switchFragment(fragment1)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.cartitem -> {
                    switchFragment(fragment2)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.pedidositem -> {
                    switchFragment(fragment3)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.favitem -> {
                    switchFragment(fragment4)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        switchFragment(fragment1)

    }

    private fun switchFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (!fragment.isAdded) {
            transaction.add(R.id.layout, fragment)
        }
        transaction.hide(activeFragment)
        transaction.show(fragment)
        transaction.commit()
        activeFragment = fragment
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
        // Borrar el estado de inicio de sesión de las SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("isLoggedIn")
        editor.apply()
        firebaseAuth.signOut()
        Toast.makeText(baseContext,"Sesion cerrada correctamente",Toast.LENGTH_SHORT).show()
        val i=Intent(this,Inicio::class.java)
        startActivity(i)
    }
}