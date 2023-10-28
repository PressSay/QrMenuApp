package com.example.qfmenu.network.services

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MenuService {
    @GET("/api/menus")
    suspend fun findALl()

    @GET("/api/menus/{id}")
    suspend fun findOne(@Path("id") id: String)

    @FormUrlEncoded
    @POST("/api/menus")
    suspend fun create(
        @Field("name") name: String,
        @Field("isUsed") isUsed: String
    )
    @FormUrlEncoded
    @PUT("/api/menus/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("newName") newName: String,
        @Field("isUsed") isUsed: String
    )

    @DELETE("/api/menus/{id}")
    suspend fun delete(@Path("id") id: String)

}