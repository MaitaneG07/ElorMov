package com.example.elormov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elormov.ConsultaAlumnosActivity
import com.example.elormov.ConsultaHorariosProfesorActivity
import com.example.elormov.MainActivity
import com.example.elormov.PerfilActivity
import com.example.elormov.R
import com.example.elormov.R.*
import com.example.elormov.ReunionesActivity

class PaginaPrincipalActivity : AppCompatActivity() {

    private var tipoDeUsuario: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_pagina_principal)

        // Recepción de datos
        val nombre = intent.getStringExtra("USER_NOMBRE") ?: "Usuario"
        val userId = intent.getIntExtra("USER_ID", -1)
        tipoDeUsuario = intent.getIntExtra("TIPO_ID", -1)

        val recyclerView = findViewById<RecyclerView>(id.recycleViewPaginaPrincipal)
        val numberOfColumns = 6
        recyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)

        val botonPerfil: ImageButton = findViewById(id.btnPerfil)
        val botonConsultar: Button = findViewById(id.buttonConsultarPP)
        val botonSalir: Button = findViewById(id.buttonSalirPP)
        val botonConsultarReuniones: Button = findViewById(id.buttonConsultarReunionesPP)
        val nombreUsuario: TextView = findViewById(id.textViewNombreUsuarioPP)

        nombreUsuario.text = nombre.trim()

        // Configuración de interfaz según tipo
        when (tipoDeUsuario) {
            3 -> botonConsultar.setText(string.boton_consultarAlumnos)
            4 -> botonConsultar.setText(string.boton_consultarHorarioProfesor)
        }

        // Prueba de objeto Serializable
        /*val usuarioRecibido = intent.getSerializableExtra("USER_DATA")
        tvPrueba.text = if (usuarioRecibido != null) {
            "USER_DATA recibido:\n$usuarioRecibido"
        } else {
            "❌ USER_DATA NO recibido"
        }*/


        // Eventos
        botonPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java).apply {
                putExtra("USER_ID", userId)
                putExtra("TIPO_USUARIO", tipoDeUsuario)
            }
            startActivity(intent)
        }

        botonConsultar.setOnClickListener {
            val destino = when (tipoDeUsuario) {
                3 -> ConsultaAlumnosActivity::class.java
                4 -> ConsultaHorariosProfesorActivity::class.java
                else -> null
            }
            destino?.let { startActivity(Intent(this, it)) }
        }

        botonConsultarReuniones.setOnClickListener {
            startActivity(Intent(this, ReunionesActivity::class.java))
        }

        botonSalir.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = position / 6    // Fila actual
        val column = position % 6 // Columna actual (0 a 5)

        when {
            // Celda superior izquierda (vacía o título)
            row == 0 && column == 0 -> {
                holder.textView.text = ""
            }

            // Encabezado Horizontal: Días de la semana (Fila 0)
            row == 0 && column > 0 -> {
                val dias = listOf("Lun", "Mar", "Mié", "Jue", "Vie")
                holder.textView.text = dias[column - 1]
                holder.itemView.setBackgroundColor(Color.LTGRAY) // Opcional: Estilo encabezado
            }

            // Encabezado Vertical: Números 1 al 7 (Columna 0)
            row > 0 && column == 0 -> {
                holder.textView.text = row.toString()
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            }

            // Celdas de Contenido: Datos del servidor
            else -> {
                // Aquí buscas en tu lista de horarios
                // El índice de tus datos sería algo como: datos[row - 1][column - 1]
                holder.textView.text = "Cita"
            }
        }
    }

    override fun getItemCount(): Int = 6 * 8 // 6 columnas * (1 encabezado + 7 filas) = 48 celdas
}