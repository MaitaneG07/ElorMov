package com.example.elormov.retrofit.entities

data class Matriculaciones(
    val id: Int,
    val users: Users,
    val ciclos: Ciclos,
    val curso: Int,
    val fecha: String
)
