package com.example.stawa
//-------------- IMPORTS Y LIBRERIAS ------------------//
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
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

//-------------- Asignacion de la fuente ----------------//
        binding.textView4.typeface= Typeface.createFromAsset(assets, "Fonts/Abel-Regular.ttf")

//-------------- Redondear -----------------//
        val myLinearLayout = findViewById<LinearLayout>(R.id.myLinearLayout)
    binding.textView6.typeface= Typeface.createFromAsset(assets, "Fonts/Abel-Regular.ttf")


}
}