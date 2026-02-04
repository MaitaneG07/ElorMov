package com.example.elormov.retrofit.entities

import com.example.elormov.retrofit.entities.Modulos
import com.example.elormov.retrofit.entities.Users
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Horarios(
    @SerializedName("id")
    val id: Int,

    @SerializedName("dia")
    val dia: String,

    @SerializedName("hora")
    val hora: Int,

    // IMPORTANTE:
    // En Java tu variable es 'private Users profesor', pero tu getter es 'getUsers()'.
    // Por eso el JSON vendrá con la clave "users".
    // Aquí en Kotlin lo llamamos 'profesor' para que sea claro, pero mapeamos a "users".
    @SerializedName("users")
    val profesor: Users?,

    // Relación con Módulos
    @SerializedName("modulos")
    val modulos: Modulos?,

    @SerializedName("aula")
    val aula: String?,

    @SerializedName("observaciones")
    val observaciones: String?,

    // Las fechas llegan como String (ej: "2023-10-20T10:00:00")
    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?

) : Serializable