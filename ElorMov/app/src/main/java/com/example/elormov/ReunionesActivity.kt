package com.example.elormov

import android.graphics.Color
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
import com.example.elormov.retrofit.entities.ReunionListaDto
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ReunionesActivity : AppCompatActivity() {

    private val dias = listOf("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES")
    private lateinit var table: TableLayout

    private var userId: Int = -1
    private var tipoId: Int = -1

    private var reuniones: List<ReunionListaDto> = emptyList()
    private var reunionSeleccionada: ReunionListaDto? = null

    private lateinit var btnAceptar: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reuniones)

        userId = intent.getIntExtra("USER_ID", -1)
        tipoId = intent.getIntExtra("TIPO_ID", -1)

        table = findViewById(R.id.tableReuniones)

        btnAceptar = findViewById(R.id.buttonAceptarReuniones)
        btnCancelar = findViewById(R.id.buttonCancelarReuniones)
        btnVolver = findViewById(R.id.buttonVolverReuniones)

        val esProfesor = (tipoId == 3)
        btnAceptar.isEnabled = esProfesor
        btnCancelar.isEnabled = esProfesor

        btnVolver.setOnClickListener { finish() }

        btnAceptar.setOnClickListener {
            val sel = reunionSeleccionada
            if (!esProfesor) return@setOnClickListener
            if (sel == null) {
                Toast.makeText(this, "Selecciona una reunión", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            cambiarEstado(sel.idReunion, "aceptada")
        }

        btnCancelar.setOnClickListener {
            val sel = reunionSeleccionada
            if (!esProfesor) return@setOnClickListener
            if (sel == null) {
                Toast.makeText(this, "Selecciona una reunión", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            cambiarEstado(sel.idReunion, "denegada")
        }

        cargarReuniones()
    }

    private fun cargarReuniones() {
        if (userId == -1) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                reuniones = RetrofitClient.reunionesInterface.getReunionesUsuario(userId)
                reunionSeleccionada = null
                pintarTabla(reuniones)
            } catch (e: Exception) {
                Toast.makeText(this@ReunionesActivity, "Error cargando reuniones", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cambiarEstado(reunionId: Int, nuevoEstado: String) {
        lifecycleScope.launch {
            try {
                val body = mapOf(
                    "profesorId" to userId,
                    "estado" to nuevoEstado
                )

                val resp = RetrofitClient.reunionesInterface.cambiarEstado(reunionId, body)
                if (resp.isSuccessful) {
                    Toast.makeText(this@ReunionesActivity, "Estado actualizado", Toast.LENGTH_SHORT).show()
                    cargarReuniones() // refresca tabla y colores
                } else {
                    Toast.makeText(this@ReunionesActivity, "No se pudo actualizar (HTTP ${resp.code()})", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReunionesActivity, "Error actualizando estado", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pintarTabla(lista: List<ReunionListaDto>) {
        while (table.childCount > 1) table.removeViewAt(1)

        val grid = Array(6) { arrayOfNulls<ReunionListaDto>(5) }

        for (r in lista) {
            val fecha = parseFecha(r.fecha) ?: continue
            val diaIdx = diaToIndex(fecha) ?: continue

            val horaSlot = horaToSlot(fecha.hour) ?: continue

            val row = horaSlot - 1
            val col = diaIdx
            if (row in 0..5 && col in 0..4) grid[row][col] = r
        }

        for (hora in 1..6) {
            val row = TableRow(this)

            row.addView(crearCelda(text = hora.toString(), isHeader = true, estado = null, reunion = null))

            for (col in 0..4) {
                val reunion = grid[hora - 1][col]
                val texto = reunion?.titulo ?: ""
                row.addView(crearCelda(text = texto, isHeader = false, estado = reunion?.estado, reunion = reunion))
            }

            table.addView(row)
        }
    }

    private fun crearCelda(text: String, isHeader: Boolean, estado: String?, reunion: ReunionListaDto?): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.setPadding(8, 8, 8, 8)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isHeader) 12f else 11f)
        tv.setTypeface(null, if (isHeader) Typeface.BOLD else Typeface.NORMAL)
        tv.maxLines = if (isHeader) 1 else 3

        if (isHeader) {
            tv.setBackgroundResource(R.drawable.bg_cell) // tu fondo de celdas
        } else {
            tv.setBackgroundColor(colorPorEstado(estado))
        }

        tv.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        if (!isHeader && reunion != null) {
            tv.setOnClickListener {
                reunionSeleccionada = reunion
                Toast.makeText(this, "Seleccionada: ${reunion.titulo}", Toast.LENGTH_SHORT).show()
            }
        }

        return tv
    }

    private fun colorPorEstado(estado: String?): Int {
        return when (estado?.lowercase()) {
            "conflicto" -> Color.parseColor("#BDBDBD") // gris
            "aceptada"  -> Color.parseColor("#4CAF50") // verde
            "denegada"  -> Color.parseColor("#F44336") // rojo
            "pendiente" -> Color.parseColor("#FF9800") // naranja
            else        -> Color.TRANSPARENT
        }
    }

    private fun parseFecha(s: String?): LocalDateTime? {
        return try {
            if (s.isNullOrBlank()) null else LocalDateTime.parse(s)
        } catch (_: Exception) { null }
    }

    private fun diaToIndex(fecha: LocalDateTime): Int? {
        return when (fecha.dayOfWeek.value) {
            1 -> 0 // L
            2 -> 1 // M
            3 -> 2 // X
            4 -> 3 // J
            5 -> 4 // V
            else -> null
        }
    }

    private fun horaToSlot(hour: Int): Int? {
        return when (hour) {
            8 -> 1
            9 -> 2
            10 -> 3
            11 -> 4
            12 -> 5
            13 -> 6
            else -> null
        }
    }
}
