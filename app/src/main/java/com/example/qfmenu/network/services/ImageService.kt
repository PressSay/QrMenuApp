package com.example.qfmenu.network.services

import com.example.qfmenu.network.ImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageService {
    @Headers("Accept: application/json")
    @POST("/api/images")
    suspend fun create(
        @Body requestBody: RequestBody
    ): ImageResponse
}