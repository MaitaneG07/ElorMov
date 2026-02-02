package com.example.elormov.retrofit.endpoints

    import retrofit2.Response
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import retrofit2.http.Body
    import retrofit2.http.POST

    interface PasswordInterface {

        @POST("api/auth/recuperar-password")
        suspend fun recuperarPassword(
            @Body request: Map<String, String>
        ): Response<String>
    }
