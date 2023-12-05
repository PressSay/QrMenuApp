package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.ListInvestment
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InvestmentService {

    @GET("/api/global/investments")
    suspend fun findALl(): Response<ListInvestment>

    @FormUrlEncoded
    @POST("/api/global/investments")
    suspend fun create(
        @Field("name") name: String,
        @Field("cost") cost: String
    ): Response<String>

    @DELETE("/api/global/investments/{id}")
    suspend fun delete(@Path("id") id: String): Response<String>
}