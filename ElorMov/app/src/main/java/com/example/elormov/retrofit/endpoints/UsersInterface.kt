package com.example.elormov.retrofit.endpoints

import com.example.elormov.retrofit.entities.PerfilAlumnoDto
import com.example.elormov.retrofit.entities.Users
import com.example.elormov.retrofit.modelo.LoginRequest
import com.example.elormov.retrofit.modelo.LoginResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UsersInterface {
    @GET("api/users")
    suspend fun getAllUsers(): List<Users>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<Users>

    @POST("/api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/users/{id}/perfil-alumno")
    suspend fun getPerfilAlumno(@Path("id") id: Int): Response<PerfilAlumnoDto>

    @Multipart
    @POST("api/users/{id}/foto")
    suspend fun uploadFoto(
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): Response<Map<String, Any>>
}