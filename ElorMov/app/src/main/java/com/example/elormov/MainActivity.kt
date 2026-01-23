package com.example.elormov


import RetrofitClient
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

    // Ajusta la IP según corresponda
    private val ipServidor = "10.5.104.25"
    private val puerto = 9000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializamos Retrofit
        try {
            RetrofitClient.init(ipServidor, puerto)
        } catch (e: Exception) {
            Toast.makeText(this, "Error config: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // Referencias UI
        // Aunque el ID del XML se llame InputEmail, aquí lo trataremos como username
        val inputUsername = findViewById<TextInputEditText>(R.id.InputEmail)
        val inputPassword = findViewById<TextInputEditText>(R.id.InputContrasenya)
        val btnIniciarSesion = findViewById<Button>(R.id.buttonMainIniciarSesion)
        val recuperarPassword = findViewById<TextView>(R.id.textRecuperarPassword)

        // 1. Cargar datos previos (username/pass)
        cargarDatosLogin(inputUsername, inputPassword)

        recuperarPassword.setOnClickListener { popUpRecuperarContrasenna() }

        // 2. Click en Login
        btnIniciarSesion.setOnClickListener {
            val username = inputUsername.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            realizarLogin(username, password)
        }
    }

    private fun realizarLogin(username: String, pass: String) {
        lifecycleScope.launch {
            try {
                // CAMBIO: Ahora enviamos username en el objeto
                val request = LoginRequest(username = username, password = pass)

                val response = RetrofitClient.usersInterface.login(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val usuarioObj = loginResponse?.user

                    if (usuarioObj != null) {
                        Toast.makeText(this@MainActivity, "Hola ${usuarioObj.nombre}", Toast.LENGTH_SHORT).show()

                        // Guardamos username en lugar de email
                        guardarDatos(username, pass)

                        val intent = Intent(this@MainActivity, PaginaPrincipalActivity::class.java)
                        intent.putExtra("USER_ID", usuarioObj.id)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Error conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Guardar en Preferencias (Username)
    private fun guardarDatos(username: String, password: String) {
        val prefs = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("username", username) // Clave cambiada a 'username'
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
            // Leemos la clave 'username'
            inputUser.setText(prefs.getString("username", ""))
            inputPass.setText(prefs.getString("password", ""))
        }
    }

    private fun popUpRecuperarContrasenna() {
        // ... (Tu código del popup igual)
    }
}