package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.RevBill
import com.example.qfmenu.network.entity.RevDish
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

    @GET("/api/global/reviews/dish")
    suspend fun findAllDish(): Response<ArrayList<RevDish>>

    @GET("/api/global/reviews/bill")
    suspend fun findAllBill(): Response<ArrayList<RevBill>>

    @GET("/api/global/reviews/dish/{dishId}/{customerId}")
    suspend fun findOneDish(@Path("dishId") dishId: String, @Path("customerId") customerId: String): Response<RevDish>

    @GET("/api/global/reviews/bill/{customerId}")
    suspend fun findOneBill(@Path("customerId") id: String): Response<RevBill>

    @FormUrlEncoded
    @POST("/api/global/reviews/bill")
    suspend fun createRevBill(@Field("customerId") customerId: String, @Field("star") star: String, @Field("description") description: String): Response<String>

    @FormUrlEncoded
    @POST("/api/global/reviews/dish")
    suspend fun createRevDish(@Field("dishId") dishId: String, @Field("customerId") customerId: String, @Field("star") star: String, @Field("description") description: String): Response<String>

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @PUT("/api/global/reviews/bill/{customerId")
    suspend fun updateRevBill(@Path("customerId") customerId: String, @Field("star") star: String, @Field("description") description: String): Response<String>

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @PUT("/api/global/reviews/dish/{dishId}/{customerId}")
    suspend fun updateRevDish(@Path("dishId") dishId: String, @Path("customerId") customerId: String, @Field("star") star: String, @Field("description") description: String): Response<String>

    @Headers("Accept: application/json")
    @DELETE("/api/global/reviews/bill/{customerId}")
    suspend fun deleteRevBill(@Path("customerId") customerId: String): Response<String>

    @Headers("Accept: application/json")
    @DELETE("/api/global/reviews/dish/{dishId}/{customerId}")
    suspend fun deleteRevDish(@Path("dishId") dishId: String, @Path("customerId") customerId: String): Response<String>

}