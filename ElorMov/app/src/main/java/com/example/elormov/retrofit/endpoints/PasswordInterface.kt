package com.example.elormov.retrofit.endpoints

import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.ResponseBody
import retrofit2.Response

interface PasswordInterface {
    @POST("api/auth/recuperar-password")
    suspend fun recuperarPassword(
        @Body request: Map<String, String>
    ): Response<ResponseBody>
}