package com.example.elormov

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
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

    private lateinit var btnCrear: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reuniones)

        userId = intent.getIntExtra("USER_ID", -1)
        tipoId = intent.getIntExtra("TIPO_ID", -1)

        table = findViewById(R.id.tableReuniones)
        btnCrear = findViewById(R.id.buttonCrearReunion)
        btnVolver = findViewById(R.id.buttonVolverReuniones)

        btnVolver.setOnClickListener { finish() }

        btnCrear.setOnClickListener {
            val i = Intent(this, CrearReunionActivity::class.java)
            i.putExtra("USER_ID", userId)
            i.putExtra("TIPO_ID", tipoId)
            startActivity(i)
        }

        cargarReuniones()
    }

    override fun onResume() {
        super.onResume()
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
                pintarTabla(reuniones)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@ReunionesActivity,
                    "Error cargando reuniones",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun cambiarEstado(reunionId: Int, nuevoEstado: String, onOk: () -> Unit) {
        lifecycleScope.launch {
            try {
                val body = com.example.elormov.retrofit.entities.EstadoUpdateDto(
                    profesorId = userId,
                    estado = nuevoEstado
                )

                RetrofitClient.reunionesInterface.cambiarEstado(reunionId, body)

                Toast.makeText(this@ReunionesActivity, "Estado actualizado", Toast.LENGTH_SHORT).show()
                onOk()
                cargarReuniones()

            } catch (e: retrofit2.HttpException) {
                e.printStackTrace()
                val codigo = e.code()
                val mensajeError = e.response()?.errorBody()?.string() ?: "Error desconocido"
                Toast.makeText(this@ReunionesActivity, "Error HTTP $codigo: $mensajeError", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ReunionesActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
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

    private fun crearCelda(
        text: String,
        isHeader: Boolean,
        estado: String?,
        reunion: ReunionListaDto?
    ): TextView {

        val tv = TextView(this)
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.setPadding(8, 8, 8, 8)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isHeader) 12f else 11f)
        tv.setTypeface(null, if (isHeader) Typeface.BOLD else Typeface.NORMAL)
        tv.maxLines = if (isHeader) 1 else 3

        if (isHeader) {
            tv.setBackgroundResource(R.drawable.bg_cell)
        } else {
            tv.setBackgroundColor(colorPorEstado(estado))
        }

        tv.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        if (!isHeader && reunion != null) {
            tv.setOnClickListener { mostrarPopupReunion(reunion) }
        }

        return tv
    }

    private fun mostrarPopupReunion(reunion: ReunionListaDto) {
        val esProfesor = (tipoId == 3)

        val titulo = reunion.titulo ?: "(Sin título)"
        val fecha = reunion.fecha ?: "-"
        val estado = reunion.estado ?: "-"

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val tvInfo = TextView(this)
        tvInfo.text = "Fecha: $fecha\nEstado actual: $estado"
        tvInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        tvInfo.setTextColor(Color.BLACK)
        tvInfo.setPadding(0, 0, 0, 30)
        layout.addView(tvInfo)

        val builder = AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(layout)

        val dialog = builder.create()

        if (esProfesor) {
            fun agregarBotonAccion(texto: String, colorHex: String, estadoAEnviar: String) {
                val btn = Button(this)
                btn.text = texto
                btn.setBackgroundColor(Color.parseColor(colorHex))
                btn.setTextColor(Color.WHITE)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 10, 0, 10)
                btn.layoutParams = params

                btn.setOnClickListener {
                    cambiarEstado(reunion.idReunion, estadoAEnviar) {
                        dialog.dismiss()
                    }
                }
                layout.addView(btn)
            }

            agregarBotonAccion("Aceptar Reunión", "#4CAF50", "aceptada")
            agregarBotonAccion("Denegar Reunión", "#F44336", "denegada")
            agregarBotonAccion("Marcar Conflicto", "#FF9800", "conflicto")
        }

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cerrar") { d, _ ->
            d.dismiss()
        }

        dialog.show()
    }

    private fun colorPorEstado(estado: String?): Int {
        return when (estado?.lowercase()) {
            "conflicto" -> Color.parseColor("#BDBDBD")
            "aceptada" -> Color.parseColor("#4CAF50")
            "denegada" -> Color.parseColor("#F44336")
            "pendiente" -> Color.parseColor("#FF9800")
            else -> Color.TRANSPARENT
        }
    }

    private fun parseFecha(s: String?): LocalDateTime? {
        return try {
            if (s.isNullOrBlank()) null else LocalDateTime.parse(s)
        } catch (_: Exception) {
            null
        }
    }

    private fun diaToIndex(fecha: LocalDateTime): Int? {
        return when (fecha.dayOfWeek.value) {
            1 -> 0
            2 -> 1
            3 -> 2
            4 -> 3
            5 -> 4
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