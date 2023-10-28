package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.CustomerIntegration
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CustomerService {
    @Headers("Accept: application/json")
    @GET("/api/customers")
    suspend fun findALl(): Response<List<CustomerIntegration>>
    @Headers("Accept: application/json")
    @GET("/api/customers/{id}")
    suspend fun findOne(@Path("id") id: String): Response<CustomerIntegration>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("/api/customers")
    suspend fun create(
        @Field("userId") userId: String,
        @Field("name") name: String,
        @Field("code") code: String,
        @Field("phoneNumber") phoneNumber: String,
        @Field("address") address: String,
        @Field("dishes") dishes: String, // change to Array Object
        @Field("promotion") promotion: String,
        @Field("statusOrder") statusOrder: String,
        @Field("payments") payments: String,
        @Field("tableId") tableId: String
    ): Response<CustomerIntegration>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @PUT("/api/customers/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("code") code: String,
        @Field("phoneNumber") phoneNumber: String,
        @Field("address") address: String,
        @Field("dishes") dishes: String, // change to Array Object
        @Field("promotion") promotion: String,
        @Field("statusOrder") statusOrder: String,
        @Field("payments") payments: String
    ): Response<CustomerIntegration>
    @Headers("Accept: application/json")
    @DELETE("/api/customers/{id}")
    suspend fun delete(@Path("id") id: String): Response<CustomerIntegration>
}