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

        lifecycleScope.launch {
            try {
                // GET /api/users/{id}
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

                    val email = u.email
                    val direccion = u.direccion ?: "-"
                    val tel1 = u.telefono1 ?: "-"
                    val tel2 = u.telefono2 ?: "-"

                    tvDatosPerfil.text =
                        "Nombre: ${if (nombreCompleto.isNotBlank()) nombreCompleto else "-"}\n" +
                                "Email: $email\n" +
                                "Dirección: $direccion\n" +
                                "Teléfono 1: $tel1\n" +
                                "Teléfono 2: $tel2"

                } else {
                    tvDatosPerfil.text = "No se pudo cargar el perfil (HTTP ${response.code()})"
                }

            } catch (e: Exception) {
                tvDatosPerfil.text = "Error: ${e.message}"
                Toast.makeText(this@PerfilActivity, "Error al cargar el perfil", Toast.LENGTH_SHORT).show()
            }
        }
    }
}