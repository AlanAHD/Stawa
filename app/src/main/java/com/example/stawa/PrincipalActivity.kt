package com.example.stawa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PrincipalActivity : AppCompatActivity() {
    private lateinit var nombre:TextView
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        firebaseAuth=Firebase.auth
        nombre=findViewById(R.id.idbienvenida)
        val user=FirebaseAuth.getInstance().currentUser
        val name=user?.displayName

        if(name != null){
            nombre.text=name
        }
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