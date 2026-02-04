package com.example.elormov.retrofit.entities

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

    @SerializedName("estado")
    val estado: String?,

    @SerializedName("estadoEus")
    val estadoEus: String?,

    @SerializedName("idCentro")
    val idCentro: String?,

    @SerializedName("profesores")
    val profesor: Users?,

    @SerializedName("alumnos")
    val alumno: Users?,

    @SerializedName("fecha")
    val fecha: String?,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt: String?

) : Serializable