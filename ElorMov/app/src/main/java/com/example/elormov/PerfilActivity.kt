package com.example.elormov

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        //recibo el tipo de usuario de la ventana anterior
        //si es profesor muestra unas cosas y si el alumno otras
        val tipoDeUsuario = intent.getIntExtra("TIPO_USUARIO", -1)
        val userId = intent.getIntExtra("USER_ID", -1)

        val botonVolver: Button = findViewById(R.id.buttonVolverPerfil)
        val tvDatosPerfil: TextView = findViewById(R.id.textViewDatosPerfil)

        botonVolver.setOnClickListener {
            finish()
        }

        // 🔒 Validación básica
        if (userId == -1) {
            tvDatosPerfil.text = "Error: usuario no válido"
            return
        }

        if (tipoDeUsuario == 3) {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.usersInterface.getUserById(userId.toLong())

                    if (response.isSuccessful) {
                        val u = response.body()
                        if (u == null) {
                            tvDatosPerfil.text = "Error: respuesta vacía"
                            return@launch
                        }

                        val nombreCompleto = listOfNotNull(u.nombre, u.apellidos)
                            .joinToString(" ")
                            .trim()

                        tvDatosPerfil.text =
                            "${getString(R.string.profesor)}\n\n" +
                                    "${if (nombreCompleto.isNotBlank()) nombreCompleto else "-"}\n\n" +
                                    "${getString(R.string.email)}: ${u.email}\n" +
                                    "${getString(R.string.direccion)}: ${u.direccion ?: "-"}\n" +
                                    "${getString(R.string.telefono)} " + "1: " + "${u.telefono1 ?: "-"}\n" +
                                    "${getString(R.string.telefono)} " + "2: " + "${u.telefono2 ?: "-"}"

                    } else {
                        tvDatosPerfil.text = "No se pudo cargar el perfil (HTTP ${response.code()})"
                    }

                } catch (e: Exception) {
                    tvDatosPerfil.text = "Error: ${e.message}"
                    Toast.makeText(this@PerfilActivity, "Error al cargar el perfil", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // ✅ ALUMNO (tipo 4) -> mostrar datos académicos (GET /api/users/{id}/perfil-alumno)
        else if (tipoDeUsuario == 4) {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.usersInterface.getPerfilAlumno(userId)

                    if (response.isSuccessful) {
                        val perfil = response.body()
                        if (perfil == null) {
                            tvDatosPerfil.text = "Error: respuesta vacía"
                            return@launch
                        }

                        tvDatosPerfil.text =
                            "${getString(R.string.alumno)}\n\n" +
                                    "${perfil.nombre ?: "-"} ${perfil.apellidos ?: ""}\n\n" +
                                    "${getString(R.string.email)}: ${perfil.email ?: "-"}\n" +
                                    "${getString(R.string.ciclo)}: ${perfil.cicloNombre}\n" +
                                    "${getString(R.string.curso)}: ${perfil.curso}º\n" +
                                    "${getString(R.string.fechaMatricula)}: ${perfil.fechaMatricula ?: "-"}"

                    } else {
                        tvDatosPerfil.text = "No se pudo cargar el perfil alumno (HTTP ${response.code()})"
                    }

                } catch (e: Exception) {
                    tvDatosPerfil.text = "Error: ${e.message}"
                    Toast.makeText(this@PerfilActivity, "Error al cargar el perfil alumno", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            tvDatosPerfil.text = "Rol desconocido (TIPO_USUARIO=$tipoDeUsuario)"
        }
    }
}