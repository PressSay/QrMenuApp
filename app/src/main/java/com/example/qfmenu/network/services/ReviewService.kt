package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.Customer
import com.example.qfmenu.network.entity.Dish
import com.example.qfmenu.network.entity.Review
import com.example.qfmenu.network.entity.ReviewCustomer
import com.example.qfmenu.network.entity.ReviewDish
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
    @Headers("Accept: application/json")
    @GET("/api/reviews?dish=1")
    suspend fun findALlDish(): Response<List<ReviewDish>>
    @Headers("Accept: application/json")
    @GET("/api/reviews?customer=1")
    suspend fun findALlCustomer(): Response<List<ReviewCustomer>>
    @Headers("Accept: application/json")
    @GET("/api/reviews/{id}")
    suspend fun findOne(@Path("id") id: String): Response<Review>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("/api/reviews")
    suspend fun create(
        @Field("forDish") forDish: String,
        @Field("dishId") dishId: String,
        @Field("customerId") customerId: String,
        @Field("isGood") isGood: String,
        @Field("description") description: String
    ): Response<String>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @PUT("/api/reviews/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("description") description: String,
        @Field("isGood") isGood: String
    ): Response<Review>
    @Headers("Accept: application/json")
    @DELETE("/api/reviews/{id}")
    suspend fun delete(@Path("id") id: String)
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @DELETE("/api/reviews")
    suspend fun deleteAll(
        @Field("dishId") dishId: String,
        @Field("customerId") customerId: String,
        @Field("forDish") forDish: String
    )
}