package com.example.voteapp.data.api

import com.example.voteapp.data.model.AuthenticatedUserDto
import com.example.voteapp.data.model.ChangePasswordDto
import com.example.voteapp.data.model.Comment
import com.example.voteapp.data.model.CommentRequestDto
import com.example.voteapp.data.model.CommentResponseDto
import com.example.voteapp.data.model.LoginRequest
import com.example.voteapp.data.model.OptionRequestDto
import com.example.voteapp.data.model.RegisterDto
import com.example.voteapp.data.model.TwoFactorLoginDto
import com.example.voteapp.data.model.UpdateProfileDto
import com.example.voteapp.data.model.UserDto
import com.example.voteapp.data.model.Vote
import com.example.voteapp.data.model.VoteDetails
import com.example.voteapp.data.model.VoteOption
import com.example.voteapp.data.model.VoteResponseDto
import com.example.voteapp.data.model.WhoVotedYetRequestDto
import com.example.voteapp.data.model.WhoVotedYetResponseDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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
    fun postComment(@Body body: CommentRequestDto): Call<CommentResponseDto>

    @DELETE("/api/vote-comments/{id}")
    fun deleteComment(@Path("id") commentId: Int): Call<Void>

    @POST("report/user/create")
    fun reportComment(@Body body: Map<String, Any>): Call<Void>

    @GET("/api/users/getAuth")
    fun getAuthenticatedUser(): Call<AuthenticatedUserDto>

    @POST("/api/vote")
    fun createVote(@Body voteData: Map<String, String>): Call<VoteResponseDto>

    @POST("/api/vote-options")
    fun addVoteOption(@Body option: OptionRequestDto): Call<Void>

    @GET("/api/profile/public/{userId}")
    fun getPublicUserProfile(@Path("userId") userId: Long): Call<UserDto>

    @GET("/api/vote/public/user/{userId}")
    fun getVotesByUser(
        @Path("userId") userId: Long,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Call<List<VoteResponseDto>>

    @GET("/api/profile/public/{id}")
    fun getPublicProfile(@Path("id") id: Long, ): Call<UserDto>

    @PUT("/api/profile/update")
    fun updateProfile(@Body updateData: UpdateProfileDto): Call<Void>

    @POST("/api/users/public/register")
    fun registerUser(@Body request: RegisterDto): Call<Void>

    @POST("/api/users/public/login/2fa")
    fun verify2FA(@Body request: TwoFactorLoginDto): Call<ResponseBody>

    @PUT("/api/users/changePassword")
    fun changePassword(@Body request: ChangePasswordDto): Call<Void>


}