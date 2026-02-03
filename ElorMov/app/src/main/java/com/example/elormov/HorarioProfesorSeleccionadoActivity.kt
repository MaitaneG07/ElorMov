package com.example.elormov

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.elormov.retrofit.entities.HorarioProfesorDto
import com.example.elormov.retrofit.entities.HorariosDto
import kotlinx.coroutines.launch

class HorarioProfesorSeleccionadoActivity : BaseActivity() {

    private val dias = listOf("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES")
    private lateinit var tableHorario: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horario_profesor_seleccionado)

        val botonVolver: Button = findViewById(R.id.buttonVolverHPS)
        val tvTitulo: TextView = findViewById(R.id.txNombreProfesorSeleccionado)
        tableHorario = findViewById(R.id.tableHorario)

        val profId = intent.getIntExtra("PROF_ID", -1)
        val profNombre = intent.getStringExtra("PROF_NOMBRE") ?: ""

        tvTitulo.text = getString(R.string.horarioDeProfesor) + profNombre

        if (profId == -1) {
            Toast.makeText(this, "Profesor inválido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        cargarHorarioProfesor(profId)

        botonVolver.setOnClickListener { finish() }
    }

    private fun cargarHorarioProfesor(profesorId: Int) {
        lifecycleScope.launch {
            try {
                val resp = RetrofitClient.horariosInterface.getHorarioProfesor(profesorId)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null) {
                        pintarHorarioEnTabla(body)
                    } else {
                        Toast.makeText(this@HorarioProfesorSeleccionadoActivity, "Respuesta vacía", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@HorarioProfesorSeleccionadoActivity, "HTTP ${resp.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@HorarioProfesorSeleccionadoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pintarHorarioEnTabla(dto: HorarioProfesorDto) {
        while (tableHorario.childCount > 1) tableHorario.removeViewAt(1)

        val grid = buildGrid(dto.slots)

        for (hora in 1..6) {
            val row = TableRow(this)

            row.addView(crearCelda(text = hora.toString(), isHeader = true))

            for (col in 0..4) {
                val slot = grid[hora - 1][col]
                row.addView(crearCelda(text = slotToText(slot), isHeader = false))
            }

            tableHorario.addView(row)
        }
    }

    private fun buildGrid(slots: List<HorariosDto>): Array<Array<HorariosDto?>> {
        val grid = Array(6) { Array<HorariosDto?>(5) { null } }
        for (s in slots) {
            val row = s.hora - 1
            val col = dias.indexOf(s.dia)
            if (row in 0..5 && col in 0..4) {
                grid[row][col] = s
            }
        }
        return grid
    }

    private fun slotToText(s: HorariosDto?): String {
        if (s == null) return ""
        return when (s.tipo) {
            "CLASE" -> {
                val aula = if (!s.aula.isNullOrBlank()) "\n${s.aula}" else ""
                "${s.curso}º ${s.ciclo}\n${s.modulo}$aula"
            }
            "TUTORIA" -> "Tutoría"
            "GUARDIA" -> "Guardia"
            else -> s.modulo ?: ""
        }
    }

    private fun crearCelda(text: String, isHeader: Boolean): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.maxLines = if (isHeader) 1 else 4
        tv.ellipsize = android.text.TextUtils.TruncateAt.END
        tv.setPadding(8, 8, 8, 8)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isHeader) 12f else 11f)
        tv.setTypeface(null, if (isHeader) Typeface.BOLD else Typeface.NORMAL)
        tv.setBackgroundResource(R.drawable.bg_cell)
        tv.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        return tv
    }
}
