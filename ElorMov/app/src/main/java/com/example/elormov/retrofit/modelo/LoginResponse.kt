package com.example.elormov.retrofit.modelo

import Users
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("user")
    val user: Users // Ojo: Esta clase 'User' la creamos en el siguiente paso
)