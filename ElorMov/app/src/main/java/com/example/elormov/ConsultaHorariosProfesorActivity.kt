package com.example.elormov

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elormov.ui.adapters.ProfesorTablaAdapter
import kotlinx.coroutines.launch

class ConsultaHorariosProfesorActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_horarios_profesor)

        val recycler = findViewById<RecyclerView>(R.id.recyclerProfesores)
        val botonVolver: Button = findViewById(R.id.buttonVolverCHP)
        val spinnerNombre = findViewById<Spinner>(R.id.spinnerNombre)
        val spinnerApellidos = findViewById<Spinner>(R.id.spinnerApellidos)
        lateinit var adapter: ProfesorTablaAdapter

        recycler.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            try {
                val profesores = RetrofitClient.usersInterface.getProfesoresTabla()

                adapter = ProfesorTablaAdapter(profesores) { profesor ->
                    val i = Intent(
                        this@ConsultaHorariosProfesorActivity,
                        HorarioProfesorSeleccionadoActivity::class.java
                    )
                    i.putExtra("PROF_ID", profesor.id)
                    i.putExtra("PROF_NOMBRE", "${profesor.nombre} ${profesor.apellidos}")
                    startActivity(i)
                }
                recycler.adapter = adapter

                // ✅ 3) Rellenar spinners
                val textoNombre = getString(R.string.nombre)       // "Nombre"
                val textoApellidos = getString(R.string.apellidos) // "Apellidos"

                val nombres = listOf(textoNombre) +
                        profesores.map { it.nombre }.distinct().sorted()

                val apellidos = listOf(textoApellidos) +
                        profesores.map { it.apellidos }.distinct().sorted()

                spinnerNombre.adapter = ArrayAdapter(
                    this@ConsultaHorariosProfesorActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    nombres
                )

                spinnerApellidos.adapter = ArrayAdapter(
                    this@ConsultaHorariosProfesorActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    apellidos
                )

                fun aplicarFiltro() {
                    val nombreSel = spinnerNombre.selectedItem.toString()
                        .takeIf { it != textoNombre }

                    val apellidosSel = spinnerApellidos.selectedItem.toString()
                        .takeIf { it != textoApellidos }

                    adapter.filtrar(nombreSel, apellidosSel)
                }

                spinnerNombre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: android.view.View?,
                        position: Int,
                        id: Long
                    ) {
                        if (spinnerNombre.selectedItem.toString() != textoNombre) {
                            spinnerApellidos.setSelection(0)
                        }
                        aplicarFiltro()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

                spinnerApellidos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: android.view.View?,
                        position: Int,
                        id: Long
                    ) {
                        if (spinnerApellidos.selectedItem.toString() != textoApellidos) {
                            spinnerNombre.setSelection(0)
                        }
                        aplicarFiltro()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

                aplicarFiltro()

            } catch (e: Exception) {
                Toast.makeText(
                    this@ConsultaHorariosProfesorActivity,
                    "Error al cargar profesores",
                    Toast.LENGTH_LONG
                ).show()
            }
        }



        botonVolver.setOnClickListener {
            finish()
        }
    }
}