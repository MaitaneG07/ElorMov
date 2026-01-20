package com.example.elormov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ReunionesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reuniones)

        val botonCrear : Button = findViewById(R.id.buttonAceptarReuniones)
        val botonVolver : Button = findViewById(R.id.buttonVolverReuniones)

        botonCrear.setOnClickListener {
            startActivity(Intent(this, CrearReunionActivity::class.java))
        }

        botonVolver.setOnClickListener {
            finish()
        }
    }
}