package com.example.qfmenu.network.services

import com.example.qfmenu.network.TokenKey
import com.example.qfmenu.network.entity.User
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Headers
import retrofit2.http.Path

interface UserService {
    @FormUrlEncoded
    @POST("/api/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<TokenKey>
    @Headers("Accept: application/json")
    @GET("/api/admin")
    suspend fun user(): Response<User>
    @Headers("Accept: application/json")
    @GET("/api/staffs")
    suspend fun findAllStaff() : Response<List<User>>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("/api/staffs")
    suspend fun create(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("level") level: Int,
        @Field("phoneNumber") phoneNumber: String,
        @Field("address") address: String
    ) : Response<User>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @GET("/api/staffs/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("level") level: Int,
        @Field("phoneNumber") phoneNumber: String,
        @Field("address") address: String
    ) : Response<User>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @DELETE("/api/staffs/{id}")
    suspend fun delete(
        @Path("id") id: String
    ): Response<User>
}