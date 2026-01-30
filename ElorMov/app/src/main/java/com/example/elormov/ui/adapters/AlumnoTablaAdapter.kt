package com.example.elormov.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elormov.R
import com.example.elormov.retrofit.entities.AlumnoTablaDto

class AlumnoTablaAdapter(
    private val alumnos: List<AlumnoTablaDto>
) : RecyclerView.Adapter<AlumnoTablaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val apellidos: TextView = view.findViewById(R.id.tvApellidos)
        val ciclo: TextView = view.findViewById(R.id.tvCiclo)
        val curso: TextView = view.findViewById(R.id.tvCurso)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno_tabla, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alumno = alumnos[position]
        holder.nombre.text = alumno.nombre
        holder.apellidos.text = alumno.apellidos
        holder.ciclo.text = alumno.ciclo
        holder.curso.text = "${alumno.curso}º"
    }

    override fun getItemCount(): Int = alumnos.size
}