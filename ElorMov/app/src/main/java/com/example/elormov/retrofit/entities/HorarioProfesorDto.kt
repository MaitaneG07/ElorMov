package com.example.elormov.retrofit.entities

data class HorarioProfesorDto(
    val profesorId: Int,
    val profesorNombre: String,
    val slots: List<HorariosDto>
)
