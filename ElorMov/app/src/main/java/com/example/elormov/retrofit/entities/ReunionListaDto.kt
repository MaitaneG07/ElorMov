package com.example.elormov.retrofit.entities

data class ReunionListaDto(
    val idReunion: Int,
    val titulo: String?,
    val fecha: String?,
    val estado: String?,
    val profesorId: Int,
    val alumnoId: Int
)
