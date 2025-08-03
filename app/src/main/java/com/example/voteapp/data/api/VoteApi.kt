package com.example.voteapp.data.api

import com.example.voteapp.data.model.LoginRequest
import com.example.voteapp.data.model.LoginResponse
import com.example.voteapp.data.model.Vote
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface VoteApi {

    @GET("/api/vote/public/all")
    fun getAllVotes(): Call<List<Vote>>

    @POST
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}