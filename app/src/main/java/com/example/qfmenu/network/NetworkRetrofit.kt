package com.example.qfmenu.network

import com.example.qfmenu.network.services.CategoryService
import com.example.qfmenu.network.services.CustomerService
import com.example.qfmenu.network.services.DishService
import com.example.qfmenu.network.services.ImageService
import com.example.qfmenu.network.services.InvestmentService
import com.example.qfmenu.network.services.MenuService
import com.example.qfmenu.network.services.ReviewService
import com.example.qfmenu.network.services.TableService
import com.example.qfmenu.network.services.UserService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class NetworkRetrofit(private val token: String = "") {
    companion object {
        const val BASE_URL =
            "http://192.168.1.6"
        const val BASE_URL_SOCKET =
            "ws://192.168.1.6:6001/app/qrmenuKey"
    }

    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())

    private val client =
        retrofitBuilder.client(OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
            request.addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/json")
            chain.proceed(request.build())
        }).build()).build()



    fun image(): ImageService {
        return client.create(ImageService::class.java)
    }

    fun user(): UserService {
        return client.create(UserService::class.java)
    }

    fun menu(): MenuService {
        return client.create(MenuService::class.java)
    }

    fun category(): CategoryService {
        return client.create(CategoryService::class.java)
    }

    fun dish(): DishService {
        return client.create(DishService::class.java)
    }

    fun table(): TableService {
        return client.create(TableService::class.java)
    }

    fun review(): ReviewService {
        return client.create(ReviewService::class.java)
    }

    fun investment(): InvestmentService {
        return client.create(InvestmentService::class.java)
    }

    fun customer(): CustomerService {
        return client.create(CustomerService::class.java)
    }

}