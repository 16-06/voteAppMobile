package com.example.voteapp.data.model

data class CommentRequestDto(
    val commentBody: String,
    val voteId: Long
)

data class CommentResponseDto(
    val id: Long,
    val commentBody: String,
    val commentAuthorId: Long,
    val voteId: Long,
    val createdAt: String,
    val commentAuthorUsername: String
)

