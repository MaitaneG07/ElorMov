package com.example.elormov.retrofit.entities


import Users
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Reuniones(
    @SerializedName("idReunion")
    val idReunion: Int,

    @SerializedName("titulo")
    val titulo: String?,

    @SerializedName("asunto")
    val asunto: String?,

    @SerializedName("aula")
    val aula: String?,

    // ESTADO
    @SerializedName("estado")
    val estado: String?,

    @SerializedName("estadoEus") // Coincide con getEstadoEus()
    val estadoEus: String?,

    @SerializedName("idCentro")
    val idCentro: String?,

    // RELACIÓN PROFESOR
    // ¡OJO! En Java es 'profesor' pero el getter es 'getProfesores()'.
    // El JSON llegará como "profesores".
    @SerializedName("profesores")
    val profesor: Users?,

    // RELACIÓN ALUMNO
    // ¡OJO! En Java es 'alumno' pero el getter es 'getAlumnos()'.
    // El JSON llegará como "alumnos".
    @SerializedName("alumnos")
    val alumno: Users?,

    // FECHAS (LocalDateTime -> String)
    @SerializedName("fecha")
    val fecha: String?,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt: String?

) : Serializable