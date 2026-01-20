package com.example.elormov.retrofit.entities

data class Modulos(
    val id: Int,
    val nombre: String,
    val nombreEus: String?,
    val horas: Int,
    val ciclos: Ciclos,
    val curso: Int
)
