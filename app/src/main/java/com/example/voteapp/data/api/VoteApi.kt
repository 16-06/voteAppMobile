package com.example.voteapp.data.api

import com.example.voteapp.data.model.LoginRequest
import com.example.voteapp.data.model.Vote
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface VoteApi {

    @GET("/api/vote/public/all")
    fun getAllVotes(): Call<List<Vote>>

    @POST("/api/users/public/login")
    fun login(@Body request: LoginRequest): Call<ResponseBody>
}