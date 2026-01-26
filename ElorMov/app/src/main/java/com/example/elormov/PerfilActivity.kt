package com.example.elormov

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class PerfilActivity : AppCompatActivity() {

    private lateinit var imagenSacada: ImageView

    private val pedirPermisoCamara =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                abrirCamara()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    private val abrirCamaraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val bmp = result.data?.extras?.get("data") as? Bitmap
                if (bmp != null) {
                    imagenSacada.setImageBitmap(bmp)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        //recibo el tipo de usuario de la ventana anterior
        //si es profesor muestra unas cosas y si el alumno otras
        val tipoDeUsuario = intent.getIntExtra("TIPO_USUARIO", -1)
        val userId = intent.getIntExtra("USER_ID", -1)

        val botonVolver: Button = findViewById(R.id.buttonVolverPerfil)
        val tvDatosPerfil: TextView = findViewById(R.id.textViewDatosPerfil)
        val botonCamara: Button = findViewById(R.id.buttonAnnadirImagen)
        imagenSacada = findViewById(R.id.imageViewPerfil)

        botonCamara.setOnClickListener {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (granted) {
                abrirCamara()
            } else {
                pedirPermisoCamara.launch(Manifest.permission.CAMERA)
            }
        }

        botonVolver.setOnClickListener {
            finish()
        }

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
                                    "${getString(R.string.telefono)} 1: ${u.telefono1 ?: "-"}\n" +
                                    "${getString(R.string.telefono)} 2: ${u.telefono2 ?: "-"}"

                    } else {
                        tvDatosPerfil.text =
                            "No se pudo cargar el perfil (HTTP ${response.code()})"
                    }

                } catch (e: Exception) {
                    tvDatosPerfil.text = "Error: ${e.message}"
                    Toast.makeText(
                        this@PerfilActivity,
                        "Error al cargar el perfil",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else if (tipoDeUsuario == 4) {
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
                        tvDatosPerfil.text =
                            "No se pudo cargar el perfil alumno (HTTP ${response.code()})"
                    }

                } catch (e: Exception) {
                    tvDatosPerfil.text = "Error: ${e.message}"
                    Toast.makeText(
                        this@PerfilActivity,
                        "Error al cargar el perfil alumno",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            tvDatosPerfil.text = "Rol desconocido (TIPO_USUARIO=$tipoDeUsuario)"
        }
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            abrirCamaraLauncher.launch(intent)
        } else {
            Toast.makeText(this, "No hay app de cámara disponible", Toast.LENGTH_SHORT).show()
        }
    }
}
