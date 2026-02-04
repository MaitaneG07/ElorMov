package com.example.elormov

import RetrofitClient
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.elormov.retrofit.endpoints.PasswordInterface
import com.example.elormov.retrofit.modelo.LoginRequest
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.elormov.retrofit.entities.Users

class MainActivity : BaseActivity() {

    private var cliente: RetrofitClient? = null

    //ip para usar el servidor en el mismo pc
    private val ipServidor = "10.0.2.2"
    //ip del servidor de Giselle:
    //private val ipServidor = "10.5.104.31" // Giselle
    //ip del servidor de Maitane:
    //private val ipServidor = "10.5.104.25" // Maitane
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

        conectarAlServidor(null)

        // 2. Referencias UI
        val inputUsuario = findViewById<TextInputEditText>(R.id.InputEmail)
        val inputPassword = findViewById<TextInputEditText>(R.id.InputContrasenya)

        // 3. Cargar datos previos si existen (Autocompletar)
        cargarDatosLogin(inputUsuario, inputPassword)

        val btnIniciarSesion = findViewById<Button>(R.id.buttonMainIniciarSesion)
        val recuperarPasswordText = findViewById<TextView>(R.id.textRecuperarPassword)

        // 4. Listener Recuperar contraseña
        recuperarPasswordText.setOnClickListener {
            popUpRecuperarContrasenna()
        }

        btnIniciarSesion.setOnClickListener {
            // Obtenemos el texto de los inputs
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

                            Toast.makeText(this@MainActivity, "Logeado con éxito", Toast.LENGTH_SHORT).show()

                            val tipoId = userOk.tipos?.id ?: -1
                            val intent = Intent(this@MainActivity, PaginaPrincipalActivity::class.java)
                            intent.putExtra("USER_ID", userOk.id)
                            intent.putExtra("USER_NOMBRE", userOk.nombre ?: "")
                            intent.putExtra("TIPO_ID", tipoId)
                            intent.putExtra("USER_DATA", usuario)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Error de conexión
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Error: ${e.javaClass.simpleName} - ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onConexionRecuperada() {
        conectarAlServidor(null)
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
            }
        }
    }

    private fun popUpRecuperarContrasenna() {
        val inputEmail = findViewById<TextInputEditText>(R.id.InputEmail)
        val username = inputEmail.text.toString()

        if (username.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu username para recuperar", Toast.LENGTH_SHORT).show()
            return
        }

        val mensaje = "$username, ¿quieres recuperar tu contraseña?"

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Recuperar contraseña")
            .setMessage(mensaje)
            .setPositiveButton("Sí") { _, _ ->
                recuperarPasswordDeVerdad(username)
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun recuperarPasswordDeVerdad(username: String) {
        // Mostrar progress dialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Enviando nueva contraseña...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Hacer la petición al servidor
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = mapOf("username" to username)
                val response = RetrofitClient.passwordInterface.recuperarPassword(request)

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Se ha enviado una nueva contraseña a tu email registrado", Toast.LENGTH_LONG).show()
                    } else {
                        val errorMsg = when (response.code()) {
                            404 -> "Usuario no encontrado"
                            400 -> "Username inválido"
                            else -> "Error al procesar la solicitud"
                        }
                        Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("RecuperarPassword", "Error: ${e.message}", e)
                }
            }
        }
    }

    @SuppressLint("UseKtx")
    private fun guardarDatos(usuario: String, password: String) {
        val prefs = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("username", usuario)
            putString("password", password)
            putBoolean("recordar", true)
            apply()
        }
    }

    // Cargar de Preferencias
    private fun cargarDatosLogin(inputUsuario: TextInputEditText, inputPassword: TextInputEditText) {
        val prefs = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val recordar = prefs.getBoolean("recordar", false)
        if (recordar) {
            val savedUser = prefs.getString("username", null) ?: prefs.getString("email", "")
            inputUsuario.setText(savedUser)
            inputPassword.setText(prefs.getString("password", ""))
        }
    }
} // Fin de la clase MainActivity