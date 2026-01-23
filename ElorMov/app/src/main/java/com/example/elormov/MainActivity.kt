package com.example.elormov

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.elormov.retrofit.modelo.LoginRequest // Asegúrate de que este import es correcto
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Configuración del servidor
    private val ipServidor = "10.0.2.2" // Localhost para el emulador
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
        val btnIniciarSesion = findViewById<Button>(R.id.buttonMainIniciarSesion)
        val recuperarPassword = findViewById<TextView>(R.id.textRecuperarPassword)

        // 3. Cargar datos previos si existen (Autocompletar)
        cargarDatosLogin(inputUsername, inputPassword)

        // 4. Listener Recuperar contraseña
        recuperarPassword.setOnClickListener {
            popUpRecuperarContrasenna()
        }

        // 5. Listener BOTÓN LOGIN
        btnIniciarSesion.setOnClickListener {
            // Obtenemos el texto de los inputs
            val usernameTexto = inputUsername.text.toString().trim()
            val passwordTexto = inputPassword.text.toString().trim()

            if (usernameTexto.isEmpty() || passwordTexto.isEmpty()) {
                Toast.makeText(this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Creamos el objeto para enviar (usando el texto, no el EditText)
            val request = LoginRequest(username = usernameTexto, password = passwordTexto)

            // Lanzamos la corrutina
            lifecycleScope.launch {
                try {
                    // Llamada al servidor
                    val response = RetrofitClient.usersInterface.login(request)

                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val usuario = loginResponse?.user

                        if (usuario != null) {
                            // A) Login correcto: Guardamos credenciales para la próxima vez
                            guardarDatos(usernameTexto, passwordTexto)

                            // B) Navegación según Tipo de Usuario
                            val tipoId = usuario.tipos?.id

                            when (tipoId) {
                                4 -> { // ALUMNO
                                    val intent = Intent(this@MainActivity, AlumnoActivity::class.java)
                                    intent.putExtra("USER_DATA", usuario)
                                    startActivity(intent)
                                    finish()
                                }
                                3 -> { // PROFESOR
                                    val intent = Intent(this@MainActivity, ProfesorActivity::class.java)
                                    intent.putExtra("USER_DATA", usuario)
                                    startActivity(intent)
                                    finish()
                                }
                                else -> {
                                    Toast.makeText(this@MainActivity, "Rol desconocido ($tipoId)", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    } else {
                        // Error del servidor (401, 404, etc.)
                        Toast.makeText(this@MainActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    // Error de conexión
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Fallo de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    } // Fin del onCreate

    // --- MÉTODOS AUXILIARES ---

    // Guardar en Preferencias
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

    private fun popUpRecuperarContrasenna() {
        Toast.makeText(this, "Funcionalidad de recuperar contraseña aquí", Toast.LENGTH_SHORT).show()
    }

} // Fin de la clase MainActivity