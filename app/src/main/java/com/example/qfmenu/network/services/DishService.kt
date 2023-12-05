package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.Dish
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DishService {

    @GET("/api/global/dishes")
    suspend fun findALl(): Response<List<Dish>>

    @GET("/api/global/dishes/{id}")
    suspend fun findOne(@Path("id") id: String): Response<Dish>

    @FormUrlEncoded
    @POST("/api/global/dishes")
    suspend fun create(
        @Field("categoryId") categoryId: String,
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("cost") cost: String,
        @Field("numberOfTimesCalled") numberOfTimesCalled: Int
    ): Response<String>

    @FormUrlEncoded
    @PUT("/api/global/dishes/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("cost") cost: String,
        @Field("numberOfTimesCalled") numberOfTimesCalled: String
    ): Response<String>

    @DELETE("/api/global/dishes/{id}")
    suspend fun delete(@Path("id") id: String): Response<String>
}