package com.example.qfmenu.network

import android.content.SharedPreferences
import android.util.Log
import com.example.qfmenu.database.entity.DishDb
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface QrApiService {
    @Multipart
    @POST("/api/uploadImageDish")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): ImageResponse

    @GET("/api/dishes")
    suspend fun getDishes(): List<DishDb>

    @FormUrlEncoded
    @POST("/api/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<TokenKey>

    @GET("/user")
    suspend fun user(): Response<UserNetwork>

}