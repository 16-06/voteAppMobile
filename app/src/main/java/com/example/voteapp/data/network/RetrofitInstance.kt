package com.example.voteapp.data.network

import android.content.Context
import com.example.voteapp.data.api.VoteApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitInstance {

    private const val BASE_URL = "http://192.168.101.11:8080"

    fun getRetrofit(context: Context):Retrofit {

        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val client = OkHttpClient.Builder().addInterceptor{
            chain ->
            val requestBuilder = chain.request().newBuilder()
            prefs.getString("jwt_token",null) ?. let {
                token -> requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }.build()

        return retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()

    }

    fun getApi(context: Context): VoteApi = getRetrofit(context).create(VoteApi::class.java)


}