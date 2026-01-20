package com.example.elormov.retrofit.entities

data class Reuniones(
    val idReunion: Int,
    val estado: String?,
    val estadoEus: String?,
    val profesor: Users?,
    val alumno: Users?,
    val idCentro: String?,
    val titulo: String?,
    val asunto: String?,
    val aula: String?,
    val fecha: String?,
    val createdAt: String?,
    val updatedAt: String?
)
