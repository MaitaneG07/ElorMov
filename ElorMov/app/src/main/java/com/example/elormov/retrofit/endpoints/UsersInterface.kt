package com.example.elormov.retrofit.endpoints

import com.example.elormov.retrofit.entities.Users
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UsersInterface {
    @GET("api/users")
    suspend fun getAllUsers(): List<Users>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<Users>
}