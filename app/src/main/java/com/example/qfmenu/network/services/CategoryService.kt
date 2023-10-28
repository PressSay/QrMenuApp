package com.example.qfmenu.network.services

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryService {
    @GET("/api/categories")
    suspend fun findALl()

    @GET("/api/categories/{id}")
    suspend fun findOne(@Path("id") id: String)

    @FormUrlEncoded
    @POST("/api/categories")
    suspend fun create(
        @Field("name") name: String,
        @Field("menuId") isUsed: String
    )
    @FormUrlEncoded
    @PUT("/api/categories/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("newName") newName: String,
    )

    @DELETE("/api/categories/{id}")
    suspend fun delete(@Path("id") id: String)
}