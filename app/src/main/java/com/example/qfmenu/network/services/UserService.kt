package com.example.qfmenu.network.services

import com.example.qfmenu.network.TokenKey
import com.example.qfmenu.network.UserNetwork
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Headers

interface UserService {
    @FormUrlEncoded
    @POST("/api/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<TokenKey>
    @Headers("Accept: application/json")
    @GET("/api/user")
    suspend fun user(): Response<UserNetwork>
}