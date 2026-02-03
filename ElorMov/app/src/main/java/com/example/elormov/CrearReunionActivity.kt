package com.example.elormov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CrearReunionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_reunion)

        val botonCrear : Button = findViewById(R.id.buttonAceptarCR)
        val botonCancelar : Button = findViewById(R.id.buttonCancelarCR)

        botonCrear.setOnClickListener {

            volverAPaginaPrincipal()
        }

        botonCancelar.setOnClickListener {

            volverAPaginaPrincipal()
        }
    }

    private fun volverAPaginaPrincipal() {
        val intent = Intent(this, PaginaPrincipalActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }
}