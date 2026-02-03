package com.example.elormov

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.elormov.retrofit.entities.Users
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class CrearReunionActivity : AppCompatActivity() {

    private lateinit var inputTitulo: EditText
    private lateinit var inputTema: EditText
    private lateinit var spinnerDia: Spinner
    private lateinit var spinnerHora: Spinner
    private lateinit var spinnerAula: Spinner
    private lateinit var spinnerUbicacion: Spinner
    private lateinit var spinnerProfesor: Spinner
    private lateinit var spinnerEstudiante: Spinner
    private lateinit var btnAceptar: Button
    private lateinit var btnCancelar: Button

    private var myUserId: Int = -1
    private var myTipoId: Int = -1

    private var listaProfesores: List<Users> = emptyList()
    private var listaAlumnos: List<Users> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_reunion)

        myUserId = intent.getIntExtra("USER_ID", -1)
        myTipoId = intent.getIntExtra("TIPO_ID", -1)

        initViews()
        initStaticSpinners()
        cargarUsuarios()

        btnCancelar.setOnClickListener { volverAPaginaPrincipal() }

        btnAceptar.setOnClickListener {
            enviarReunion()
        }
    }

    private fun initViews() {
        inputTitulo = findViewById(R.id.editTextTextMultiLine)
        inputTema = findViewById(R.id.editTextTextMultiLine2)
        spinnerDia = findViewById(R.id.spinnerDiaCR)
        spinnerHora = findViewById(R.id.spinnerHoraCR)
        spinnerAula = findViewById(R.id.spinnerAulaCR)
        spinnerUbicacion = findViewById(R.id.spinnerUbicacionCR)
        spinnerProfesor = findViewById(R.id.spinnerProfesorCR)
        spinnerEstudiante = findViewById(R.id.spinnerEstudianteCR)
        btnAceptar = findViewById(R.id.buttonAceptarCR)
        btnCancelar = findViewById(R.id.buttonCancelarCR)
    }

    private fun initStaticSpinners() {
        val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
        val horas = listOf("1ª Hora (08:00)", "2ª Hora (09:00)", "3ª Hora (10:00)", "4ª Hora (11:30)", "5ª Hora (12:30)", "6ª Hora (13:30)")
        val aulas = listOf("Aula B101", "Aula 106", "5.005", "5.012", "Online")
        val ubicaciones = listOf("Edificio A", "Edificio B", "Online")

        llenarSpinner(spinnerDia, dias)
        llenarSpinner(spinnerHora, horas)
        llenarSpinner(spinnerAula, aulas)
        llenarSpinner(spinnerUbicacion, ubicaciones)
    }

    private fun cargarUsuarios() {
        lifecycleScope.launch {
            try {
                val todos = RetrofitClient.usersInterface.getAllUsers()

                listaProfesores = todos.filter { it.tipos?.id == 3 }
                listaAlumnos = todos.filter { it.tipos?.id == 4 }

                val nombresProjes = listaProfesores.map { "${it.nombre} ${it.apellidos}" }
                val nombresAlumnos = listaAlumnos.map { "${it.nombre} ${it.apellidos}" }

                llenarSpinner(spinnerProfesor, nombresProjes)
                llenarSpinner(spinnerEstudiante, nombresAlumnos)

                autoseleccionarUsuario()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@CrearReunionActivity, "Error cargando usuarios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun autoseleccionarUsuario() {
        if (myTipoId == 3) {
            val index = listaProfesores.indexOfFirst { it.id == myUserId }
            if (index != -1) {
                spinnerProfesor.setSelection(index)
                spinnerProfesor.isEnabled = false
            }
        } else if (myTipoId == 4) {
            val index = listaAlumnos.indexOfFirst { it.id == myUserId }
            if (index != -1) {
                spinnerEstudiante.setSelection(index)
                spinnerEstudiante.isEnabled = false
            }
        }
    }

    private fun enviarReunion() {
        val titulo = inputTitulo.text.toString()
        val tema = inputTema.text.toString()

        if (titulo.isEmpty() || tema.isEmpty()) {
            Toast.makeText(this, "Por favor rellena título y tema", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaProfesores.isEmpty() || listaAlumnos.isEmpty()) {
            Toast.makeText(this, "Cargando datos...", Toast.LENGTH_SHORT).show()
            return
        }

        val idProfesor = listaProfesores[spinnerProfesor.selectedItemPosition].id
        val idAlumno = listaAlumnos[spinnerEstudiante.selectedItemPosition].id

        val diaTexto = spinnerDia.selectedItem.toString()
        val horaTexto = spinnerHora.selectedItem.toString()
        val fechaIso = calcularFechaIso(diaTexto, horaTexto)

        val datos = mutableMapOf<String, Any>(
            "titulo" to titulo,
            "fecha" to fechaIso,
            "aula" to spinnerAula.selectedItem.toString(),
            "ubicacion" to spinnerUbicacion.selectedItem.toString(),
            "estado" to "pendiente",
            "asunto" to tema,
            "descripcion" to tema,
            "tema" to tema,
            "profesorId" to idProfesor,
            "profesor_id" to idProfesor,
            "alumnoId" to idAlumno,
            "alumno_id" to idAlumno,
            "alum_id" to idAlumno
        )

        lifecycleScope.launch {
            try {
                println("ENVIANDO JSON: $datos")

                val response = RetrofitClient.reunionesInterface.crearReunion(datos)

                if (response.isSuccessful) {
                    Toast.makeText(this@CrearReunionActivity, "¡Reunión creada!", Toast.LENGTH_LONG).show()
                    volverAPaginaPrincipal()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(this@CrearReunionActivity, "Error ${response.code()}: $errorBody", Toast.LENGTH_LONG).show()
                    println("ERROR SERVIDOR: $errorBody")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@CrearReunionActivity, "Fallo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calcularFechaIso(diaSemana: String, horaTexto: String): String {
        val horaLimpia = when {
            horaTexto.contains("08:00") -> "08:00:00"
            horaTexto.contains("09:00") -> "09:00:00"
            horaTexto.contains("10:00") -> "10:00:00"
            horaTexto.contains("11:30") -> "11:30:00"
            horaTexto.contains("12:30") -> "12:30:00"
            horaTexto.contains("13:30") -> "13:30:00"
            else -> "08:00:00"
        }

        val targetDayOfWeek = when (diaSemana.lowercase()) {
            "lunes" -> DayOfWeek.MONDAY
            "martes" -> DayOfWeek.TUESDAY
            "miércoles", "miercoles" -> DayOfWeek.WEDNESDAY
            "jueves" -> DayOfWeek.THURSDAY
            "viernes" -> DayOfWeek.FRIDAY
            else -> DayOfWeek.MONDAY
        }

        val fecha = LocalDate.now().with(TemporalAdjusters.nextOrSame(targetDayOfWeek))

        return "${fecha}T$horaLimpia"
    }

    private fun llenarSpinner(spinner: Spinner, datos: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun volverAPaginaPrincipal() {
        val intent = Intent(this, PaginaPrincipalActivity::class.java)
        intent.putExtra("USER_ID", myUserId)
        intent.putExtra("TIPO_ID", myTipoId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }
}