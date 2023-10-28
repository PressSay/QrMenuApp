package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.Image
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ImageService {
    @Headers("Accept: application/json")
    @POST("/api/images")
    suspend fun create(
        @Body requestBody: RequestBody
    ): Response<Image>
}