package com.example.elormov.retrofit.endpoints

import com.example.elormov.retrofit.entities.Users
import retrofit2.http.GET

interface UsersInterface {
    @GET("api/users")
    suspend fun getAllUsers(): List<Users>
}