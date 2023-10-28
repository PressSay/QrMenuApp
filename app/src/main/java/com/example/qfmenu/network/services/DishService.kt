package com.example.qfmenu.network.services

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DishService {
    @GET("/api/dishes")
    suspend fun findALl()

    @GET("/api/dishes/{id}")
    suspend fun findOne(@Path("id") id: String)

    @FormUrlEncoded
    @POST("/api/dishes")
    suspend fun create(
        @Field("categoryId") categoryId: String,
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("cost") cost: String,
        @Field("numberOfTimesCalled") numberOfTimesCalled: String
    )
    @FormUrlEncoded
    @PUT("/api/dishes/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("cost") cost: String,
        @Field("numberOfTimesCalled") numberOfTimesCalled: String
    )

    @DELETE("/api/dishes/{id}")
    suspend fun delete(@Path("id") id: String)
}