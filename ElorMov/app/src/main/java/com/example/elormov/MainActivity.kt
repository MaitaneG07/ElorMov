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
    private val ipServidor = "10.0.2.2"
    //ip del servidor de Giselle:
    //private val ipServidor = "10.5.104.31"
    //ip del servidor de Maitane:
    //private val ipServidor = "10.5.104.25"
    //cambiar puerto cuando sea necesario
    private val puerto = 9000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inicializar Retrofit
        try {
            RetrofitClient.init(ipServidor, puerto)
        } catch (e: Exception) {
            Toast.makeText(this, "Error config: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // 2. Referencias UI
        val inputUsername = findViewById<TextInputEditText>(R.id.InputEmail) // XML ID se mantiene
        val inputPassword = findViewById<TextInputEditText>(R.id.InputContrasenya)
        cargarDatosLogin(inputUsername, inputPassword)

        val btnIniciarSesion = findViewById<Button>(R.id.buttonMainIniciarSesion)
        val recuperarPassword = findViewById<TextView>(R.id.textRecuperarPassword)

        // 3. Cargar datos previos si existen (Autocompletar)
        cargarDatosLogin(inputUsername, inputPassword)

        // 4. Listener Recuperar contraseña
        recuperarPassword.setOnClickListener {
            popUpRecuperarContrasenna()
        }

        btnIniciarSesion.setOnClickListener {
            // Obtenemos el texto de los inputs
            val usernameTexto = inputUsername.text.toString().trim()
            val passwordTexto = inputPassword.text.toString().trim()

            if (usernameTexto.isEmpty() || passwordTexto.isEmpty()) {
                Toast.makeText(this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val request = LoginRequest(username = usernameTexto, password = passwordTexto)

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.usersInterface.login(request)

                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val userOk = loginResponse?.user

                        if (userOk != null) {
                            guardarDatos(usernameTexto, passwordTexto)

                            Toast.makeText(
                                this@MainActivity,
                                "Logeado con éxito",
                                Toast.LENGTH_SHORT
                            ).show()

                            val tipoId = userOk.tipos?.id ?: -1

                            val intent =
                                Intent(this@MainActivity, PaginaPrincipalActivity::class.java)
                            intent.putExtra("USER_ID", userOk.id)
                            intent.putExtra("USER_NOMBRE", userOk.nombre ?: "")
                            intent.putExtra("TIPO_ID", tipoId)
                            intent.putExtra("USER_DATA", usernameTexto)
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
                    // Error de conexión
                    e.printStackTrace()
                    Toast.makeText(
                        this@MainActivity,
                        "Fallo de conexión: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    } // Fin del onCreate

    private fun conectarAlServidor(txtEstado: TextView?) {
        txtEstado?.text = "Estado: Conectando..."

        lifecycleScope.launch {
            try {
                // "Ping": si esto responde, hay conexión al backend
                RetrofitClient.usersInterface.getAllUsers()

                txtEstado?.text = "Estado: Conectado"
            } catch (e: Exception) {
                txtEstado?.text = "Estado: Error de conexión"
                Toast.makeText(
                    this@MainActivity,
                    "No se pudo conectar con el servidor",
                    Toast.LENGTH_SHORT
                ).show()
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
    private fun guardarDatos(username: String, password: String) {
        val prefs = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("username", username)
            putString("password", password)
            putBoolean("recordar", true)
            apply()
        }
    }

    // Cargar de Preferencias
    private fun cargarDatosLogin(inputUser: TextInputEditText, inputPass: TextInputEditText) {
        val prefs = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val recordar = prefs.getBoolean("recordar", false)
        if (recordar) {
            inputUser.setText(prefs.getString("username", ""))
            inputPass.setText(prefs.getString("password", ""))
        }
    }

} // Fin de la clase MainActivity