package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.Investment
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InvestmentService {
    @Headers("Accept: application/json")
    @GET("/api/investments")
    suspend fun findALl(): Response<List<Investment>>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("/api/investments")
    suspend fun create(
        @Field("name") name: String,
        @Field("cost") cost: String
    ): Response<Investment>
    @Headers("Accept: application/json")
    @DELETE("/api/investments/{id}")
    suspend fun delete(@Path("id") id: String): Response<Investment>
}