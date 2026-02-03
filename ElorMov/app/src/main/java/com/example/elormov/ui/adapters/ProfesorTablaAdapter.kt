package com.example.elormov.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elormov.R
import com.example.elormov.retrofit.entities.ProfesorTablaDto

class ProfesorTablaAdapter(
    private val profesoresOriginales: List<ProfesorTablaDto>,
    private val onClick: (ProfesorTablaDto) -> Unit
) : RecyclerView.Adapter<ProfesorTablaAdapter.ViewHolder>() {

    private val profesoresFiltrados = profesoresOriginales.toMutableList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreProfesor: TextView =
            view.findViewById(R.id.tvNombreProfesorCompleto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesor_tabla, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profesor = profesoresFiltrados[position]

        val nombreCompleto = listOf(
            profesor.nombre,
            profesor.apellidos
        )
            .filter { it.isNotBlank() }
            .joinToString(" ")

        holder.tvNombreProfesor.text = nombreCompleto

        holder.itemView.setOnClickListener {
            onClick(profesor)
        }
    }

    override fun getItemCount(): Int = profesoresFiltrados.size

    fun filtrar(nombre: String?, apellidos: String?) {
        profesoresFiltrados.clear()

        profesoresFiltrados.addAll(
            profesoresOriginales.filter { p ->
                val okNombre = nombre == null || p.nombre == nombre
                val okApellidos = apellidos == null || p.apellidos == apellidos
                okNombre && okApellidos
            }
        )

        notifyDataSetChanged()
    }
}

