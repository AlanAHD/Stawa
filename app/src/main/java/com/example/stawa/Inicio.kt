package com.example.stawa
//-------------- IMPORTS Y LIBRERIAS ------------------//
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.stawa.databinding.ActivityInicioBinding

//---------------- INICIO DEL CODIGO -----------------------//
class Inicio : AppCompatActivity() {
//-------- Variable Binding para ligar a los elementos graficos ------//
    private lateinit var binding: ActivityInicioBinding

// ----------- oncreate ----------------//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//----------------- Implementacion de la raiz Binding ---------------------//
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

    // Si el usuario ya está conectado, redirigir a PrincipalActivity
    if (isLoggedIn) {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }
//-------------- Asignacion de la fuente ----------------//
        binding.textView4.typeface= Typeface.createFromAsset(assets, "Fonts/Abel-Regular.ttf")
        binding.textView6.typeface=Typeface.createFromAsset(assets, "Fonts/Abel-Regular.ttf")
        binding.textView5.typeface=Typeface.createFromAsset(assets, "Fonts/Abel-Regular.ttf")
        binding.textView7.typeface=Typeface.createFromAsset(assets, "Fonts/Abel-Regular.ttf")

//-------------- Redondear -----------------//
        val myLinearLayout = findViewById<LinearLayout>(R.id.myLinearLayout)
        binding.textView6.typeface= Typeface.createFromAsset(assets, "Fonts/Abel-Regular.ttf")

    // Carga la imagen desde los recursos

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
    binding.banner.setImageBitmap(blurredBitmap)

        binding.botonRegistro.setOnClickListener(){
            val intent: Intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}