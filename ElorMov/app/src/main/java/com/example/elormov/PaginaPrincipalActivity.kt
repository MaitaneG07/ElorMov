package com.example.elormov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class PaginaPrincipalActivity : AppCompatActivity() {

    // ✅ Ahora se recibe del login
    private var tipoDeUsuario: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagina_principal)

        val nombre = intent.getStringExtra("USER_NOMBRE") ?: ""
        val userId = intent.getIntExtra("USER_ID", -1)

        tipoDeUsuario = intent.getIntExtra("TIPO_ID", -1)

        val botonPerfil: ImageButton = findViewById(R.id.btnPerfil)
        val botonConsultar: Button = findViewById(R.id.buttonConsultarPP)
        val botonSalir: Button = findViewById(R.id.buttonSalirPP)
        val botonConsultarReuniones: Button = findViewById(R.id.buttonConsultarReunionesPP)
        val nombreUsuario: TextView = findViewById(R.id.textViewNombreUsuarioPP)
        val tvPrueba: TextView = findViewById(R.id.textViewPrueba)

        nombreUsuario.text = "$nombre".trim()

        //dependiendo de si entra un alumno o un profesor
        if (tipoDeUsuario == 3) {
            // Profesor
            botonConsultar.setText(R.string.boton_consultarAlumnos)
        } else if (tipoDeUsuario == 4) {
            // Alumno
            botonConsultar.setText(R.string.boton_consultarHorarioProfesor)
        }

        //empieza la prueba
        val usuarioRecibido = intent.getSerializableExtra("USER_DATA")

        if (usuarioRecibido != null) {
            tvPrueba.text =
                "USER_DATA recibido correctamente:\n\n" +
                        usuarioRecibido.toString()
        } else {
            tvPrueba.text = "❌ USER_DATA NO recibido"
        }
        //termina prueba

        botonPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("TIPO_USUARIO", tipoDeUsuario)
            startActivity(intent)
        }

        botonConsultar.setOnClickListener {
            if (tipoDeUsuario == 3) {
                startActivity(Intent(this, ConsultaAlumnosActivity::class.java))
            } else if (tipoDeUsuario == 4) {
                startActivity(Intent(this, ConsultaHorariosProfesorActivity::class.java))
            }
        }

        botonConsultarReuniones.setOnClickListener {
            startActivity(Intent(this, ReunionesActivity::class.java))
        }

        botonSalir.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
