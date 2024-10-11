package com.nudriin.restaurantreview.data.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
        // Create logging interceptor
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        // Create retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://restaurant-api.dicoding.dev/")
            .addConverterFactory(GsonConverterFactory.create()) // add gson convert
            .client(client) // add ok http
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        return apiService
        }
    }
}