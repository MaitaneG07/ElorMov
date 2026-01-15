package com.example.elormov.retrofit.client

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080") // localhost from the Android emulator
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}