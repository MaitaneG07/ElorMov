package com.example.elormov.retrofit.entities

data class PerfilAlumnoDto(
    val userId: Int,
    val nombre: String?,
    val apellidos: String?,
    val email: String?,
    val cicloId: Int,
    val cicloNombre: String,
    val curso: Int,
    val fechaMatricula: String?
)
