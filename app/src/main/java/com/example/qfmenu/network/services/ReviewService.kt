package com.example.qfmenu.network.services

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReviewService {
    @GET("/api/reviews")
    suspend fun findALl()

    @GET("/api/reviews/{id}")
    suspend fun findOne(@Path("id") id: String)

    @FormUrlEncoded
    @POST("/api/reviews")
    suspend fun create(
        @Field("forDish") forDish: String,
        @Field("dishId") dishId: String,
        @Field("customerId") customerId: String,
        @Field("isGood") isGood: String,
        @Field("description") description: String
    )
    @FormUrlEncoded
    @PUT("/api/reviews/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("description") description: String,
        @Field("isGood") isGood: String
    )

    @DELETE("/api/reviews/{id}")
    suspend fun delete(@Path("id") id: String)

    @FormUrlEncoded
    @DELETE("/api/reviews")
    suspend fun deleteAll(
        @Field("dishId") dishId: String,
        @Field("customerId") customerId: String,
        @Field("forDish") forDish: String
    )
}