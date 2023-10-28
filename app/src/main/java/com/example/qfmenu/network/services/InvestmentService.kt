package com.example.qfmenu.network.services

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InvestmentService {
    @GET("/api/investments")
    suspend fun findALl()

    @FormUrlEncoded
    @POST("/api/investments")
    suspend fun create(
        @Field("name") name: String,
        @Field("cost") cost: String
    )

    @DELETE("/api/investments/{id}")
    suspend fun delete(@Path("id") id: String)
}