package com.example.myapplication

import android.R.attr.value
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.back).setOnClickListener{
            val myIntent: Intent = Intent(
                this.baseContext,
                MainActivity::class.java
            )
            myIntent.putExtra("key", value) //Optional parameters
            this.startActivity(myIntent)
        }
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val nameText = findViewById<TextView>(R.id.nameText)
        val descriptionText = findViewById<TextView>(R.id.descriptionText)
        val currentJobText = findViewById<TextView>(R.id.currentJobText)
        val experienceLayout = findViewById<LinearLayout>(R.id.exp)
        nameText.text = "Ana Silva"
        descriptionText.text = "Desenvolvedora de software com 5 anos de experiência."
        currentJobText.text = "Emprego Atual: Engenheira de Software na TechX"

        // Lista de experiências
        val experiencias = listOf(
            "Analista de Sistemas - Empresa A",
            "Desenvolvedora Júnior - Empresa B",
            "Estagiária - Empresa C"
        )

        // Adicionando experiências dinamicamente
        for (experiencia in experiencias) {
            val textView = TextView(this)
            textView.text = experiencia
            textView.textSize = 16f
            experienceLayout.addView(textView)
        }

        profileImage.setOnClickListener {
            Toast.makeText(this, "foto de perfil", Toast.LENGTH_SHORT).show()
        }

    }
}