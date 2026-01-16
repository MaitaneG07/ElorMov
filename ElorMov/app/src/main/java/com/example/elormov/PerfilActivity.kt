package com.example.elormov

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        //recibo el tipo de usuario de la ventana anterior
        //si es profesor muestra unas cosas y si el alumno otras
        val tipoDeUsuario = intent.getIntExtra("TIPO_USUARIO", -1)

        val botonVolver: Button = findViewById(R.id.buttonVolverPerfil)

        botonVolver.setOnClickListener {
            finish()
        }
    }
}