package com.example.elormov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAceptar : Button = findViewById(R.id.buttonMainAceptar)
        val btnRecuperar = findViewById<Button?>(R.id.buttonRecuperarPassword)

        btnRecuperar.setOnClickListener {
            popUpRecuperarContrasenna()
        }

        btnAceptar.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun popUpRecuperarContrasenna() {

        val inputEmail = findViewById<TextInputEditText>(R.id.InputEmail)
        val email = inputEmail.text.toString()

        val mensaje = if (email.isNotEmpty()) {
            "$email, ${getString(R.string.recuperarPasswordPregunta)}"
        } else {
            getString(R.string.recuperarPasswordPregunta)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.recuperarPassword))
            .setMessage(mensaje)
            .setPositiveButton(R.string.si) { _, _ ->

                //Aqui va el codigo relacionado con lo de recuperar la contraseña
                Toast.makeText(
                    this,
                    R.string.enviarNuevoPassword,
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}