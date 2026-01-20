package com.example.elormov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CrearReunionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_reunion)

        val botonCrear : Button = findViewById(R.id.buttonAceptarCR)
        val botonCancelar : Button = findViewById(R.id.buttonCancelarCR)

        botonCrear.setOnClickListener {
            startActivity(Intent(this, PaginaPrincipalActivity::class.java))
            finish()
        }

        botonCancelar.setOnClickListener {
            startActivity(Intent(this, PaginaPrincipalActivity::class.java))
            finish()
        }
    }
}