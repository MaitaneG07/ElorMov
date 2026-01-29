package com.example.elormov.retrofit.entities

data class HorariosDto(
    val dia: String,
    val hora: Int,
    val tipo: String,
    val curso: Int?,
    val ciclo: String?,
    val modulo: String,
    val aula: String?,
    val observaciones: String?
)
