package com.example.elormov.retrofit.endpoints

import com.example.elormov.retrofit.entities.HorarioProfesorDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HorariosInterface {
    @GET("api/horarios/profesor/{id}")
    suspend fun getHorarioProfesor(
        @Path("id") profesorId: Int
    ): Response<HorarioProfesorDto>

    @GET("api/horarios/alumno/{id}")
    suspend fun getHorarioAlumno(
        @Path("id") alumnoId: Int
    ): Response<HorarioProfesorDto>
}