package com.example.elormov

import android.app.AlertDialog
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
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.elormov.config.AppConfig

class ConsultaAlumnosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_alumnos)

        val botonVolver: Button = findViewById(R.id.buttonVolverCA)
        val recycler: RecyclerView = findViewById(R.id.recyclerAlumnos)
        val spinnerCiclo: Spinner = findViewById(R.id.spinnerCiclo)
        val spinnerCurso: Spinner = findViewById(R.id.spinnerCurso)

        recycler.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            try {
                val alumnos: List<AlumnoTablaDto> =
                    RetrofitClient.usersInterface.getAlumnosTabla()

                val adapter = AlumnoTablaAdapter(alumnos) { alumno ->
                    mostrarPopupAlumno(alumno)
                }

                recycler.adapter = adapter

                val textoCiclo = getString(R.string.ciclo)
                val textoCurso = getString(R.string.curso)

                val ciclos = listOf(textoCiclo) + alumnos.map { it.ciclo }.distinct().sorted()
                val cursos = listOf(textoCurso, "1", "2")

                spinnerCiclo.adapter = ArrayAdapter(
                    this@ConsultaAlumnosActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    ciclos
                )

                spinnerCurso.adapter = ArrayAdapter(
                    this@ConsultaAlumnosActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    cursos
                )

                fun aplicarFiltro() {
                    val cicloSel = spinnerCiclo.selectedItem.toString().takeIf { it != textoCiclo }

                    val cursoSel = spinnerCurso.selectedItem.toString()
                        .takeIf { it != textoCurso }
                        ?.toInt()

                    adapter.filtrar(cicloSel, cursoSel)
                }

                val listener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                        aplicarFiltro()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

                spinnerCiclo.onItemSelectedListener = listener
                spinnerCurso.onItemSelectedListener = listener

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

    private fun mostrarPopupAlumno(alumno: AlumnoTablaDto) {
        val view = layoutInflater.inflate(R.layout.dialog_alumno, null)

        val img = view.findViewById<ImageView>(R.id.imgAlumno)
        val tv = view.findViewById<TextView>(R.id.tvNombreCompleto)

        tv.text = "${alumno.nombre} ${alumno.apellidos}"

        if (!alumno.argazkiaUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(AppConfig.BASE_URL + alumno.argazkiaUrl)
                .placeholder(R.drawable.perfilsinimagen)
                .into(img)
        } else {
            img.setImageResource(R.drawable.perfilsinimagen)
        }
        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

}