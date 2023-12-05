package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.RevCusInter
import com.example.qfmenu.network.entity.RevDishInter
import com.example.qfmenu.network.entity.Review
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReviewService {

    @GET("/api/global/reviews")
    suspend fun findAll(): Response<ArrayList<Review>>

    @GET("/api/global/reviews?dish=1")
    suspend fun findAllDish(): Response<ArrayList<RevDishInter>>

    @GET("/api/global/reviews?customer=1")
    suspend fun findAllCustomer(): Response<ArrayList<RevCusInter>>

    @GET("/api/global/reviews/{id}")
    suspend fun findOne(@Path("id") id: String): Response<Review>

    @GET("/api/global/reviews/{id}?dish=1")
    suspend fun findOneDish(@Path("id") id: String): Response<RevDishInter>

    @GET("/api/global/reviews/{id}?customer=1")
    suspend fun findOneCustomer(@Path("id") id: String): Response<RevCusInter>

    @FormUrlEncoded
    @POST("/api/global/reviews")
    suspend fun create(
        @Field("forDish") forDish: String,
        @Field("dishId") dishId: String,
        @Field("customerId") customerId: String,
        @Field("isGood") isGood: String,
        @Field("description") description: String
    ): Response<String>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @PUT("/api/global/reviews/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("description") description: String,
        @Field("isGood") isGood: String
    ): Response<Review>
    @Headers("Accept: application/json")
    @DELETE("/api/global/reviews/{id}")
    suspend fun delete(@Path("id") id: String): Response<String>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @DELETE("/api/global/reviews")
    suspend fun deleteAll(
        @Field("dishId") dishId: String,
        @Field("customerId") customerId: String,
        @Field("forDish") forDish: String
    ): Response<String>
}