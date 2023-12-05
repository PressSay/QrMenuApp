package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.Table
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TableService {

    @GET("/api/global/tables")
    suspend fun findALl(): Response<ArrayList<Table>>

    @GET("/api/global/tables/{id}")
    suspend fun findOne(@Path("id") id: String): Response<Table>

    @FormUrlEncoded
    @POST("/api/global/tables")
    suspend fun create(
        @Field("numberTable") numberTable: Int,
    ): Response<String>

    @FormUrlEncoded
    @PUT("/api/global/tables/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("status") status: String,
    ): Response<String>

}