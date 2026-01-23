package com.example.elormov.retrofit.modelo

import com.google.gson.annotations.SerializedName

// LoginRequest.kt
data class LoginRequest(
    @SerializedName("username") // IMPORTANTE: Enviamos la clave "username"
    val username: String,

    @SerializedName("password")
    val password: String
)