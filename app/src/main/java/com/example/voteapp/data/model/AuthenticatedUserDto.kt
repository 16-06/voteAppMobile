package com.example.voteapp.data.model

data class AuthenticatedUserDto(
    val id: Long,
    val username: String,
    val token: String?
)
