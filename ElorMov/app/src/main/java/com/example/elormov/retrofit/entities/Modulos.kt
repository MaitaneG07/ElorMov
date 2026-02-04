package com.example.elormov.retrofit.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Modulos(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String,

    // En Java es nombreEus y el getter getNombreEus(), así que el JSON es camelCase
    @SerializedName("nombreEus")
    val nombreEus: String?,

    @SerializedName("horas")
    val horas: Int,

    @SerializedName("curso")
    val curso: Int,

    // RELACIÓN CON CICLOS
    // Asegúrate de tener el archivo Ciclos.kt creado (lo hicimos en el paso anterior)
    @SerializedName("ciclos")
    val ciclos: Ciclos?

) : Serializable
