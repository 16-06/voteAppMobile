package com.example.voteapp.data.api

import com.example.voteapp.data.model.Comment
import com.example.voteapp.data.model.LoginRequest
import com.example.voteapp.data.model.Vote
import com.example.voteapp.data.model.VoteDetails
import com.example.voteapp.data.model.VoteOption
import com.example.voteapp.data.model.WhoVotedYetRequestDto
import com.example.voteapp.data.model.WhoVotedYetResponseDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VoteApi {

    @GET("/api/vote/public/all")
    fun getAllVotes(): Call<List<Vote>>

    @POST("/api/users/public/login")
    fun login(@Body request: LoginRequest): Call<ResponseBody>

    @GET("/api/vote-options/{id}")
    fun getVoteOptions(@Path("id") voteId: Int): Call<List<VoteOption>>

    @GET("/api/vote/byId/{id}")
    fun getVoteDetails(@Path("id") voteId: Int): Call<VoteDetails>

    @GET("/api/whoVoted/{voteId}")
    fun hasUserVoted(@Path("voteId") voteId: Long): Call<Map<String, Boolean>>

    @POST("/api/vote-options/count")
    fun voteOption(@Body body: Map<String, Long>): Call<Void>

    @POST("/api/whoVoted")
    fun markAsVoted(@Body request: WhoVotedYetRequestDto): Call<WhoVotedYetResponseDto>

    @GET("/api/vote-comments/byVoteId/{id}")
    fun getComments(@Path("id") voteId: Int): Call<List<Comment>>

    @POST("/api/vote-comments")
    fun postComment(@Body body: Map<String, Any>): Call<Comment>

    @DELETE("/api/vote-comments/{id}")
    fun deleteComment(@Path("id") commentId: Int): Call<Void>

    @POST("report/user/create")
    fun reportComment(@Body body: Map<String, Any>): Call<Void>
}