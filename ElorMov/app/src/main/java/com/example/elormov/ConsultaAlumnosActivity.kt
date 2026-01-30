package com.example.elormov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elormov.retrofit.entities.AlumnoTablaDto
import com.example.elormov.ui.adapters.AlumnoTablaAdapter
import kotlinx.coroutines.launch

class ConsultaAlumnosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_alumnos)

        val botonVolver: Button = findViewById(R.id.buttonVolverCA)
        val recycler: RecyclerView = findViewById(R.id.recyclerAlumnos)

        recycler.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            try {
                val alumnos: List<AlumnoTablaDto> =
                    RetrofitClient.usersInterface.getAlumnosTabla()

                recycler.adapter = AlumnoTablaAdapter(alumnos)

            } catch (e: Exception) {
                Toast.makeText(
                    this@ConsultaAlumnosActivity,
                    "Error al cargar la lista de alumnos",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        botonVolver.setOnClickListener {
            finish()
        }
    }
}