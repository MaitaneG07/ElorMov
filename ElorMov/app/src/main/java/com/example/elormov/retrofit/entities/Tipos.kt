package com.example.elormov.retrofit.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Tipos(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("nameEu") // Spring Boot lo envía así por el getter getNameEu()
    val nameEu: String?       // Puede ser nulo según tu entidad Java
) : Serializable