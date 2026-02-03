package com.example.elormov.retrofit.endpoints

import com.example.elormov.retrofit.entities.ReunionListaDto
import retrofit2.Response
import retrofit2.http.*

interface ReunionesInterface {

    @GET("api/reuniones/usuario/{id}")
    suspend fun getReunionesUsuario(@Path("id") id: Int): List<ReunionListaDto>

    @PATCH("api/reuniones/{reunionId}/estado")
    suspend fun cambiarEstado(
        @Path("reunionId") reunionId: Int,
        @Body body: Map<String, Any>
    ): Response<Map<String, Any>>
}