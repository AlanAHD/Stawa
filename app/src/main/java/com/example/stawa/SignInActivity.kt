package com.example.stawa

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.stawa.databinding.ActivitySignInBinding
import com.example.stawa.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class SignInActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener


    private lateinit var binding: ActivitySignInBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // Si el usuario ya está conectado, redirigir a PrincipalActivity
        if (isLoggedIn) {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }
//----------------- Imagen borrosa en el banner superior ------------------//

        // Carga la imagen desde los recursos
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.imagehamburgerban)

        // Crea una instancia de RenderScript y ScriptIntrinsicBlur

        // Crea una instancia de RenderScript y ScriptIntrinsicBlur
        val renderScript = RenderScript.create(this)
        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        // Crea un bitmap con la misma resolución que la imagen

        // Crea un bitmap con la misma resolución que la imagen
        val blurredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

        // Crea un Allocation con el bitmap

        // Crea un Allocation con el bitmap
        val allocationIn = Allocation.createFromBitmap(renderScript, bitmap)
        val allocationOut = Allocation.createFromBitmap(renderScript, blurredBitmap)

        // Establece el radio del blur y aplica el efecto

        // Establece el radio del blur y aplica el efecto
        scriptIntrinsicBlur.setRadius(25f)
        scriptIntrinsicBlur.setInput(allocationIn)
        scriptIntrinsicBlur.forEach(allocationOut)

        // Convierte el Allocation de salida en un Bitmap y muestra en el ImageView

        // Convierte el Allocation de salida en un Bitmap y muestra en el ImageView
        allocationOut.copyTo(blurredBitmap)
        binding.banner2.setImageBitmap(blurredBitmap)

        binding.crearcuentaB.setOnClickListener(){
            val intent: Intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()

        }
        val btningresar: Button= findViewById(R.id.idinicio)
        val txtemail: TextView = findViewById(R.id.idcorreo)
        val txtcontra: TextView=findViewById(R.id.idcontra)
        val btnRecordar: Button =findViewById(R.id.olvidar)
        firebaseAuth=Firebase.auth
        btningresar.setOnClickListener()
        {
            val email= txtemail.text.toString()
            val contra=txtcontra.text.toString()
            if (email.isEmpty() || contra.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                if(email.isEmpty()){
                    txtemail.error="Ingresa un correo electronico"
                }
                if(contra.isEmpty()){
                    txtcontra.error="Ingresa una contraseña"
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() and !email.isEmpty()){
                    txtemail.error="El correo electronico no es válido"

                }
            }else if(contra.length <6){
                txtcontra.error="La contraseña debe tener 6 o mas caracteres"
            }else{
                SignIn(txtemail.text.toString(),txtcontra.text.toString())
            }
        }

        btnRecordar.setOnClickListener(){
            val i = Intent(this,OlvidarPassActivity::class.java)
            startActivity(i)
        }

    }



    private fun SignIn(email:String, password:String){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
            Task->
            if(Task.isSuccessful){
                val user = firebaseAuth.currentUser
                val verificar= user?.isEmailVerified
                if(verificar==true){
                    // Guardar el estado de inicio de sesión en SharedPreferences
                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()

                    Toast.makeText(baseContext,"Autentificacion exitosa",Toast.LENGTH_SHORT).show()
                    val i = Intent(this,PrincipalActivity::class.java)
                    startActivity(i)
                    finish()
                }else{
                    Toast.makeText(baseContext,"Debes verificar tu correo electronico",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(baseContext,"Error Correo o contraseña incorrectos",Toast.LENGTH_SHORT).show()
            }
        }
    }
}