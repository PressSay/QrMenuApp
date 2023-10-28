package com.example.qfmenu.network.services

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TableService {
    @GET("/api/tables")
    suspend fun findALl()

    @GET("/api/tables/{id}")
    suspend fun findOne(@Path("id") id: String)

    @FormUrlEncoded
    @POST("/api/tables")
    suspend fun create(
        @Field("numberTable") numberTable: String,
    )
    @FormUrlEncoded
    @PUT("/api/tables/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("status") status: String,
    )

}