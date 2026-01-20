package com.example.elormov.retrofit.entities

import java.time.LocalDateTime

data class Horarios(
    val id: Int,
    val dia: String,
    val hora: Int,
    val users: Users,
    val modulos: Modulos,
    val aula: String?,
    val observaciones: String?,
    val createdAt: String?,
    val updatedAt: String?
)

