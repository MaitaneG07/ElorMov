package com.example.elormov.retrofit.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Ciclos(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String
) : Serializable
