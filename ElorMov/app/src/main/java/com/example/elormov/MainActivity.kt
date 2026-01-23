package com.example.elormov

import RetrofitClient
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.elormov.retrofit.modelo.LoginRequest
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var cliente: RetrofitClient? = null

    //ip para usar el servidor en el mismo pc
   // private val ipServidor = "10.0.2.2"
    //ip del servidor de Akira:
    //private val ipServidor = "10.5.104.32"
    //ip del servidor de Giselle:
    private val ipServidor = "10.5.104.31"
    //ip del servidor de Maitane:
    //private val ipServidor = "10.5.104.25"
    //cambiar puerto cuando sea necesario
    private val puerto = 9000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RetrofitClient.init(ipServidor, puerto)

        conectarAlServidor(null)

        val inputUsuario = findViewById<TextInputEditText>(R.id.InputEmail)
        val inputPassword = findViewById<TextInputEditText>(R.id.InputContrasenya)
        cargarDatosLogin(inputUsuario, inputPassword)

        val btnIniciarSesion = findViewById<Button>(R.id.buttonMainIniciarSesion)
        val recuperarPassword = findViewById<TextView>(R.id.textRecuperarPassword)

        recuperarPassword.setOnClickListener {
            popUpRecuperarContrasenna()
        }

        btnIniciarSesion.setOnClickListener {
            val usuario = inputUsuario.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(username = usuario, password = password)

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.usersInterface.login(request)

                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val userOk = loginResponse?.user

                        if (userOk != null) {
                            guardarDatos(usuario, password)

                            Toast.makeText(
                                this@MainActivity,
                                "Logeado con éxito",
                                Toast.LENGTH_SHORT
                            ).show()

                            val tipoId = userOk.tipos?.id ?: -1

                            val intent = Intent(this@MainActivity, PaginaPrincipalActivity::class.java)
                            intent.putExtra("USER_ID", userOk.id)
                            intent.putExtra("USER_NOMBRE", userOk.nombre ?: "")
                            intent.putExtra("TIPO_ID", tipoId)
                            intent.putExtra("USER_DATA", usuario)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Respuesta inválida del servidor",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${e.javaClass.simpleName} - ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun conectarAlServidor(txtEstado: TextView?) {
        txtEstado?.text = "Estado: Conectando..."

        lifecycleScope.launch {
            try {
                // "Ping": si esto responde, hay conexión al backend
                RetrofitClient.usersInterface.getAllUsers()

                txtEstado?.text = "Estado: Conectado"
            } catch (e: Exception) {
                txtEstado?.text = "Estado: Error de conexión"
                Toast.makeText(this@MainActivity, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun popUpRecuperarContrasenna() {
        val inputEmail = findViewById<TextInputEditText>(R.id.InputEmail)
        val email = inputEmail.text.toString()

        val mensaje = if (email.isNotEmpty()) {
            "$email, ¿quieres recuperar tu contraseña?"
        } else {
            "¿Quieres recuperar tu contraseña?"
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Recuperar contraseña")
            .setMessage(mensaje)
            .setPositiveButton("Sí") { _, _ ->
                Toast.makeText(this, "Se enviará un nuevo password", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("UseKtx")
    private fun guardarDatos(email: String, password: String) {
        val prefs = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("email", email)
            putString("password", password)
            putBoolean("recordar", true)
            apply()
        }
    }

    private fun cargarDatosLogin(
        inputUsuario: TextInputEditText,
        inputPassword: TextInputEditText
    ) {
        val prefs = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        val recordar = prefs.getBoolean("recordar", false)
        if (recordar) {
            inputUsuario.setText(prefs.getString("email", ""))
            inputPassword.setText(prefs.getString("password", ""))
        }
    }
}
