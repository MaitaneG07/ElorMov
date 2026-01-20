package com.example.elormov

import RetrofitClient
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var cliente: RetrofitClient? = null

    //private val ipServidor = "10.0.2.2"
    //ip del servidor de Giselle:
    private val ipServidor = "10.5.104.31"
    //cambiar puerto cuando sea necesario
    private val puerto = 9000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RetrofitClient.init(ipServidor, puerto)

        conectarAlServidor(null)

        val inputUsuario = findViewById<TextInputEditText>(R.id.InputEmail)
        val inputPassword = findViewById<TextInputEditText>(R.id.InputContrasenya)
        val btnAceptar = findViewById<Button>(R.id.buttonMainAceptar)
        val btnRecuperar = findViewById<Button>(R.id.buttonRecuperarPassword)

        btnRecuperar.setOnClickListener {
            popUpRecuperarContrasenna()
        }

        //para poder pasar a la siguiente ventana sin login
        /*btnAceptar.setOnClickListener {
            Toast.makeText(this, "Login desactivado (modo pruebas)", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, PaginaPrincipalActivity::class.java))
            finish()
        }*/

        //comentado para poder usarlo sin login
        /*btnAceptar.setOnClickListener {
            val usuario = inputUsuario.text.toString()
            val password = inputPassword.text.toString()

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cliente == null || !cliente!!.estaConectado()) {
                Toast.makeText(this, "No hay conexión. Intente reconectar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Thread {
                try {
                    val respuesta = cliente!!.enviarYRecibir("LOGIN:$usuario:$password")

                    runOnUiThread {
                        when (respuesta) {
                            "OK" -> {
                                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, PaginaPrincipalActivity::class.java))
                                finish()
                            }
                            "ERROR" -> {
                                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this, "No se recibió respuesta del servidor", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }*/
        btnAceptar.setOnClickListener {
            val usuario = inputUsuario.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Trae todos los usuarios del backend
                    val users = RetrofitClient.usersInterface.getAllUsers()

                    // Busca por email O username y compara password
                    val userOk = users.firstOrNull { u ->
                        (u.email.equals(usuario, ignoreCase = true) ||
                                u.username.equals(usuario, ignoreCase = true)) &&
                                u.password == password
                    }

                    if (userOk != null) {
                        Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                        // (Opcional) Pasar datos a la siguiente activity
                        val intent = Intent(this@MainActivity, PaginaPrincipalActivity::class.java)
                        intent.putExtra("USER_ID", userOk.id)
                        intent.putExtra("USER_NOMBRE", userOk.nombre ?: "")
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun conectarAlServidor(txtEstado: TextView?) {
        /*Thread {
            cliente = RetrofitClient(ipServidor, puerto)
            val conectado = cliente!!.conectar()

            runOnUiThread {
                if (conectado) {
                    txtEstado?.text = "Estado: Conectado"
                } else {
                    txtEstado?.text = "Estado: Error de conexión"
                    Toast.makeText(this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()*/
        //codigo de prueba
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
}
