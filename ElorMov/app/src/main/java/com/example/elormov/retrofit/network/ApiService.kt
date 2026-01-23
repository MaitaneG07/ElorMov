package com.example.elormov.retrofit.network

import com.example.elormov.retrofit.modelo.LoginRequest
import com.example.elormov.retrofit.modelo.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // Coincide con tu @PostMapping("/login")
    // La ruta base se define después, aquí solo pones la parte final
    @POST("/api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
