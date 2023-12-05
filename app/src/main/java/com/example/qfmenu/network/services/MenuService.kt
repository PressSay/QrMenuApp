package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.Menu
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MenuService {

    @GET("/api/global/menus")
    suspend fun findALl(): Response<List<Menu>>

    @GET("/api/global/menus/{id}")
    suspend fun findOne(@Path("id") id: String): Response<Menu>

    @FormUrlEncoded
    @POST("/api/global/menus")
    suspend fun create(
        @Field("name") name: String,
        @Field("isUsed") isUsed: Int
    )

    @FormUrlEncoded
    @PUT("/api/global/menus/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("newName") newName: String,
        @Field("isUsed") isUsed: String
    )

    @DELETE("/api/global/menus/{id}")
    suspend fun delete(@Path("id") id: String)

}