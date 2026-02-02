package com.example.elormov.retrofit.endpoints

import com.example.elormov.retrofit.entities.ReunionListaDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ReunionesInterface {

    @GET("api/reuniones/usuario/{id}")
    suspend fun getReunionesUsuario(@Path("id") userId: Int): List<ReunionListaDto>
}