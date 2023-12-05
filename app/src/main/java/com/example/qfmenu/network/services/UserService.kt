package com.example.qfmenu.network.services

import com.example.qfmenu.network.TokenKey
import com.example.qfmenu.network.entity.AuthSocket
import com.example.qfmenu.network.entity.Staff
import com.example.qfmenu.network.entity.User
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @FormUrlEncoded
    @Headers("/api/broadcasting/auth")
    suspend fun authSocket(
        @Field("channel_name") channel_name: String,
        @Field("socket_id") socket_id: String
    ): Response<AuthSocket>
    @FormUrlEncoded
    @POST("/api/global/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<TokenKey>
    @Headers("Accept: application/json")
    @GET("/api/global/admin")
    suspend fun user(): Response<User>
    @Headers("Accept: application/json")
    @GET("/api/global/admin/{id}")
    suspend fun findOne(@Path("id") id: String): Response<User>
    @Headers("Accept: application/json")
    @GET("/api/global/staffs")
    suspend fun findAllStaff() : Response<Staff>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("/api/global/staffs")
    suspend fun create(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") password_confirmation: String,
        @Field("level") level: Int,
        @Field("phoneNumber") phoneNumber: String,
        @Field("address") address: String
    ) : Response<User>
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @GET("/api/global/staffs/{id}")
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
    @DELETE("/api/global/staffs/{id}")
    suspend fun delete(
        @Path("id") id: String
    ): Response<User>
}