package com.example.elormov

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.elormov.retrofit.entities.HorarioProfesorDto
import com.example.elormov.retrofit.entities.HorariosDto
import kotlinx.coroutines.launch

class PaginaPrincipalActivity : BaseActivity() {

    private var tipoDeUsuario: Int = -1
    private val dias = listOf("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES")
    private lateinit var tableHorario: TableLayout

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
        tableHorario = findViewById(R.id.tableHorario)

        // UI
        nombreUsuario.text = nombre.trim()

        // Botón según el tipo de usuario
        if (tipoDeUsuario == 3) {
            // Profesor
            botonConsultar.setText(R.string.boton_consultarAlumnos)
        } else if (tipoDeUsuario == 4) {
            // Alumno
            botonConsultar.setText(R.string.boton_consultarHorarioProfesor)
        }

        if (userId != -1) {
            if (tipoDeUsuario == 3) {
                // PROFESOR
                cargarHorarioProfesor(userId)
            } else if (tipoDeUsuario == 4) {
                // ALUMNO
                cargarHorarioAlumno(userId)
            }
        }

        // Listeners
        botonPerfil.setOnClickListener {
            val i = Intent(this, PerfilActivity::class.java)
            i.putExtra("USER_ID", userId)
            i.putExtra("TIPO_USUARIO", tipoDeUsuario)
            startActivity(i)
        }

        botonConsultar.setOnClickListener {
            if (tipoDeUsuario == 3) {
                startActivity(Intent(this, ConsultaAlumnosActivity::class.java))
            } else if (tipoDeUsuario == 4) {
                startActivity(Intent(this, ConsultaHorariosProfesorActivity::class.java))
            }
        }

        botonConsultarReuniones.setOnClickListener {
            val i = Intent(this, ReunionesActivity::class.java)
            i.putExtra("USER_ID", userId)
            i.putExtra("TIPO_ID", tipoDeUsuario)
            startActivity(i)
        }

        botonSalir.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun cargarHorarioProfesor(profesorId: Int) {
        lifecycleScope.launch {
            try {
                val resp = RetrofitClient.horariosInterface.getHorarioProfesor(profesorId)

                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null) {
                        pintarHorarioEnTabla(body)
                    } else {
                        mostrarError("No se ha recibido el horario")
                    }
                } else {
                    manejarErrorHttp(resp.code())
                }

            } catch (e: Exception) {
                manejarErrorException(e)
            }
        }
    }

    private fun cargarHorarioAlumno(alumnoId: Int) {
        lifecycleScope.launch {
            try {
                val resp = RetrofitClient.horariosInterface.getHorarioAlumno(alumnoId)

                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null) {
                        pintarHorarioEnTabla(body)
                    } else {
                        mostrarError("No se ha recibido el horario del alumno")
                    }
                } else {
                    manejarErrorHttp(resp.code())
                }

            } catch (e: Exception) {
                manejarErrorException(e)
            }
        }
    }

    private fun pintarHorarioEnTabla(dto: HorarioProfesorDto) {
        while (tableHorario.childCount > 1) {
            tableHorario.removeViewAt(1)
        }

        val grid = buildGrid(dto.slots)

        for (hora in 1..6) {
            val row = TableRow(this)

            row.addView(crearCelda(text = hora.toString(), isHeader = true))

            for (col in 0..4) {
                val slot = grid[hora - 1][col]
                row.addView(crearCelda(text = slotToText(slot), isHeader = false))
            }

            tableHorario.addView(row)
        }
    }

    private fun buildGrid(slots: List<HorariosDto>): Array<Array<HorariosDto?>> {
        val grid = Array(6) { Array<HorariosDto?>(5) { null } }
        for (s in slots) {
            val row = s.hora - 1
            val col = dias.indexOf(s.dia)
            if (row in 0..5 && col in 0..4) {
                grid[row][col] = s
            }
        }
        return grid
    }

    private fun slotToText(s: HorariosDto?): String {
        if (s == null) return ""

        return when (s.tipo) {
            "CLASE" -> {
                val aula = if (!s.aula.isNullOrBlank()) "\n${s.aula}" else ""
                "${s.curso}º ${s.ciclo}\n${s.modulo}$aula"
            }
            "TUTORIA" -> "Tutoría"
            "GUARDIA" -> "Guardia"
            else -> s.modulo ?: ""
        }
    }

    private fun crearCelda(text: String, isHeader: Boolean): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.maxLines = if (isHeader) 1 else 4
        tv.ellipsize = android.text.TextUtils.TruncateAt.END
        tv.setPadding(8, 8, 8, 8)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isHeader) 12f else 11f)
        tv.setTypeface(null, if (isHeader) Typeface.BOLD else Typeface.NORMAL)
        tv.setBackgroundResource(R.drawable.bg_cell)
        tv.layoutParams = TableRow.LayoutParams(
            0,
            TableRow.LayoutParams.WRAP_CONTENT,
            1f
        )
        return tv
    }

    private fun manejarErrorHttp(code: Int) {
        val mensaje = when (code) {
            401 -> "Sesión caducada. Vuelve a iniciar sesión"
            403 -> "No tienes permiso para ver este horario"
            404 -> "Horario no encontrado"
            500 -> "Error interno del servidor"
            else -> "Error del servidor (código $code)"
        }
        mostrarError(mensaje)
    }

    private fun manejarErrorException(e: Exception) {
        val mensaje = when (e) {
            is java.net.UnknownHostException ->
                "No hay conexión con el servidor"

            is java.net.SocketTimeoutException ->
                "Tiempo de espera agotado"

            is com.google.gson.JsonSyntaxException ->
                "Error al procesar los datos"

            else ->
                "Error inesperado: ${e.localizedMessage}"
        }

        mostrarError(mensaje)
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
