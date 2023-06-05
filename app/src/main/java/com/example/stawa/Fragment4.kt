package com.example.stawa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri
import android.widget.Button
import com.example.stawa.databinding.Fragment1Binding
import com.example.stawa.databinding.Fragment4Binding


class Fragment4 : Fragment() {
    private var _binding: Fragment4Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = Fragment4Binding.inflate(inflater, container, false)

        binding.interfazB.setOnClickListener(){
            val  phn = "+522791164035"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data=Uri.parse("https://api.whatsapp.com/send?phone=$phn")
            startActivity(intent)
        }
        binding.funcionalidadB.setOnClickListener(){
            val  phn = "+5219242417055"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data=Uri.parse("https://api.whatsapp.com/send?phone=$phn")
            startActivity(intent)
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}