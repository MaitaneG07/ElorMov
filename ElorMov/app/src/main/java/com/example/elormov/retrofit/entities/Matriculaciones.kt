package com.example.elormov.retrofit.entities

import Users
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Matriculaciones(
    @SerializedName("id")
    val id: Int,

    // RELACIÓN CON ALUMNO (Users)
    // En Java tu variable es 'users' y el getter 'getUsers()', así que el JSON es "users"
    @SerializedName("users")
    val alumno: Users?, // Lo llamo 'alumno' en Kotlin para que sea más claro qué es

    // RELACIÓN CON CICLOS
    @SerializedName("ciclos")
    val ciclos: Ciclos?,

    @SerializedName("curso")
    val curso: Int,

    // FECHA: LocalDate llega como String (ej: "2023-09-15")
    @SerializedName("fecha")
    val fecha: String?

) : Serializable
