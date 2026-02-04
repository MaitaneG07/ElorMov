package com.example.elormov.retrofit.modelo

import com.example.elormov.retrofit.entities.Users
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("user")
    val user: Users
)