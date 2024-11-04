package com.example.practico4.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://apicontactos.jmacboy.com/"

    // Create a Retrofit instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Ensure the base URL ends with a /
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Create an ApiService instance using the Retrofit instance
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
