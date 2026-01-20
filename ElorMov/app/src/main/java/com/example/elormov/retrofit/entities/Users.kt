package com.example.elormov.retrofit.entities

data class Users(
    val id: Int,
    val email: String,
    val username: String,
    val password: String,
    val nombre: String?,
    val apellidos: String?,
    val dni: String?,
    val direccion: String?,
    val telefono1: String?,
    val telefono2: String?,
    val tipos: Tipos?,
    val argazkiaUrl: String?,
    val createdAt: String?,
    val updatedAt: String?
)
