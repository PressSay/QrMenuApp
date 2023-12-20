package com.example.qfmenu.network.services

import com.example.qfmenu.network.entity.Customer
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

    @GET("/api/global/customers")
    suspend fun findALl(): Response<ArrayList<Customer>>

    @GET("/api/global/customers/{id}")
    suspend fun findOne(@Path("id") id: String): Response<Customer>

    @FormUrlEncoded
    @POST("/api/global/customers")
    suspend fun create(
        @Field("userId") userId: Long,
        @Field("name") name: String,
        @Field("code") code: String,
        @Field("phoneNumber") phoneNumber: String,
        @Field("address") address: String,
        @Field("dishes") dishes: String, // change to Array Object
        @Field("promotion") promotion: Byte,
        @Field("statusOrder") statusOrder: String,
        @Field("payments") payments: String,
        @Field("tableId") tableId: String,
        @Field("created") created: String,
    ): Response<String>

    @FormUrlEncoded
    @PUT("/api/global/customers/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("code") code: String,
        @Field("phoneNumber") phoneNumber: String,
        @Field("address") address: String,
        @Field("dishes") dishes: String, // change to Array Object
        @Field("promotion") promotion: Byte,
        @Field("statusOrder") statusOrder: String,
        @Field("payments") payments: String,
    ): Response<String>

    @Headers("Accept: application/json")
    @DELETE("/api/global/customers/{id}")
    suspend fun delete(@Path("id") id: String)
}