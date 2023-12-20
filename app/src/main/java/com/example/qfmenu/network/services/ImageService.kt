package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.Image
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ImageService {

    @POST("/api/global/images")
    suspend fun create(
        @Body requestBody: RequestBody
    ): Response<String>
}