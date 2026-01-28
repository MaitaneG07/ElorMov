package com.example.elormov

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.example.elormov.config.AppConfig

class PerfilActivity : AppCompatActivity() {

    private lateinit var imagenSacada: ImageView
    private var userId: Int = -1
    private var tipoDeUsuario: Int = -1

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

                    subirImagenAlServidor(bmp)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        //recibo el tipo de usuario de la ventana anterior
        //si es profesor muestra unas cosas y si el alumno otras
        tipoDeUsuario = intent.getIntExtra("TIPO_USUARIO", -1)
        userId = intent.getIntExtra("USER_ID", -1)

        val botonVolver: Button = findViewById(R.id.buttonVolverPerfil)
        val tvDatosPerfil: TextView = findViewById(R.id.textViewDatosPerfil)
        val botonCamara: Button = findViewById(R.id.buttonAnnadirImagen)
        imagenSacada = findViewById(R.id.imageViewPerfil)
        val spinnerIdioma : Spinner = findViewById(R.id.spinnerIdioma)
        val idiomas = listOf("ES", "EUS", "ENG")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            idiomas
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerIdioma.adapter = adapter

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

                        val fotoUrl = u.argazkiaUrl
                        if (!fotoUrl.isNullOrBlank()) {
                            Glide.with(this@PerfilActivity)
                                .load(AppConfig.BASE_URL + fotoUrl)
                                .placeholder(R.drawable.perfilsinimagen)
                                .into(imagenSacada)
                        } else {
                            imagenSacada.setImageResource(R.drawable.perfilsinimagen)
                        }

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

                        val respUser = RetrofitClient.usersInterface.getUserById(userId.toLong())
                        if (respUser.isSuccessful) {
                            val u = respUser.body()
                            val fotoUrl = u?.argazkiaUrl

                            if (!fotoUrl.isNullOrBlank()) {
                                Glide.with(this@PerfilActivity)
                                    .load(AppConfig.BASE_URL + fotoUrl)
                                    .placeholder(R.drawable.perfilsinimagen)
                                    .into(imagenSacada)
                            } else {
                                imagenSacada.setImageResource(R.drawable.perfilsinimagen)
                            }
                        } else {
                            imagenSacada.setImageResource(R.drawable.perfilsinimagen)
                        }

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

    private fun subirImagenAlServidor(bitmap: Bitmap) {
        lifecycleScope.launch {
            try {
                val file = File(cacheDir, "perfil_${System.currentTimeMillis()}.jpg")
                file.outputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
                }

                val reqBody = file
                    .asRequestBody("image/*".toMediaTypeOrNull())

                val part = MultipartBody.Part
                    .createFormData("file", file.name, reqBody)

                val response = RetrofitClient.usersInterface
                    .uploadFoto(userId.toLong(), part)

                if (!response.isSuccessful) {
                    Toast.makeText(this@PerfilActivity, "Error subiendo imagen", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@PerfilActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLang(lang: String) {
        val locale = java.util.Locale(lang) // "en" para inglés, "es" para español, "eus" para euskera
        java.util.Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        recreate()
    }

}
