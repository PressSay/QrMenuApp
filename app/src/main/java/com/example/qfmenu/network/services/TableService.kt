package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.Table
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TableService {
    @Headers("Accept: application/json")
    @GET("/api/tables")
    suspend fun findALl(): Response<List<Table>>
    @Headers("Accept: application/json")
    @GET("/api/tables/{id}")
    suspend fun findOne(@Path("id") id: String): Response<Table>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("/api/tables")
    suspend fun create(
        @Field("numberTable") numberTable: String,
    ): Response<List<Table>>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @PUT("/api/tables/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("status") status: String,
    ): Response<Table>

}