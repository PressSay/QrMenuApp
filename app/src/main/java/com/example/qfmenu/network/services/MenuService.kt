package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.Menu
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MenuService {
    @Headers("Accept: application/json")
    @GET("/api/menus")
    suspend fun findALl(): Response<List<Menu>>
    @Headers("Accept: application/json")
    @GET("/api/menus/{id}")
    suspend fun findOne(@Path("id") id: String): Response<Menu>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("/api/menus")
    suspend fun create(
        @Field("name") name: String,
        @Field("isUsed") isUsed: String
    ): Response<Menu>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @PUT("/api/menus/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("newName") newName: String,
        @Field("isUsed") isUsed: String
    ): Response<Menu>
    @Headers("Accept: application/json")
    @DELETE("/api/menus/{id}")
    suspend fun delete(@Path("id") id: String): Response<Menu>

}