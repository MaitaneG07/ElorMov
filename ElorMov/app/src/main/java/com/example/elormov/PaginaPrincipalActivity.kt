package com.example.elormov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PaginaPrincipalActivity : AppCompatActivity() {

    //variable de prueba para seber si es profesor(3) o alumno(4), mas adelante la recibe del login
    private var tipoDeUsuario : Int = 3;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagina_principal)

        val botonPerfil: ImageButton = findViewById(R.id.btnPerfil)
        val botonConsultar : Button = findViewById(R.id.buttonConsultarPP)
        val botonSalir : Button = findViewById(R.id.buttonSalirPP)
        val botonConsultarReuniones : Button = findViewById(R.id.buttonConsultarReunionesPP)

        //dependiendo de si entra un alumno o un profesor
        if (tipoDeUsuario == 3) {
            // Profesor
            botonConsultar.setText(R.string.boton_consultarAlumnos)
        } else if (tipoDeUsuario == 4) {
            // Alumno
            botonConsultar.setText(R.string.boton_consultarHorarioProfesor)
        }

        botonPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
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