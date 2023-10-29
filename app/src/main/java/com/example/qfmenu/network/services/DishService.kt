package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.Dish
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DishService {
    @Headers("Accept: application/json")
    @GET("/api/dishes")
    suspend fun findALl(): Response<List<Dish>>
    @Headers("Accept: application/json")
    @GET("/api/dishes/{id}")
    suspend fun findOne(@Path("id") id: String): Response<Dish>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("/api/dishes")
    suspend fun create(
        @Field("categoryId") categoryId: String,
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("cost") cost: String,
        @Field("numberOfTimesCalled") numberOfTimesCalled: Int
    ): Response<List<Dish>>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @PUT("/api/dishes/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("cost") cost: String,
        @Field("numberOfTimesCalled") numberOfTimesCalled: String
    ): Response<Dish>
    @Headers("Accept: application/json")
    @DELETE("/api/dishes/{id}")
    suspend fun delete(@Path("id") id: String): Response<Dish>
}