package com.example.qfmenu.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkRetrofit {
    val BASE_URL =
        "http://192.168.1.6"

    private val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())

    fun retrofit(): QrApiService {
        return retrofitBuilder
            .baseUrl(BASE_URL)
            .build()
            .create(QrApiService::class.java)
    }
    fun retrofitToken(token: String) : QrApiService {
        return retrofitBuilder.client(
            OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
                val request = chain.request().newBuilder()
                request.addHeader("Authorization", "Bearer $token")
                chain.proceed(request.build())
            }).build()
        )
            .baseUrl(BASE_URL)
            .build()
            .create(QrApiService::class.java)
    }

}