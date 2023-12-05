package com.example.qfmenu.network.services


import com.example.qfmenu.network.entity.Category
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryService {

    @GET("/api/global/categories")
    suspend fun findALl(): Response<Array<Category>>

    @GET("/api/global/categories/{id}")
    suspend fun findOne(@Path("id") id: String): Response<Category>

    @FormUrlEncoded
    @POST("/api/global/categories")
    suspend fun create(
        @Field("name") name: String,
        @Field("menuId") menuId: String
    )

    @FormUrlEncoded
    @PUT("/api/global/categories/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Field("newName") newName: String,
    )

    @DELETE("/api/global/categories/{id}")
    suspend fun delete(@Path("id") id: String): Response<String>
}