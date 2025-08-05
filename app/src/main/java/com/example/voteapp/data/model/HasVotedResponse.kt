package com.example.voteapp.data.model

data class HasVotedResponse(

    val id: Long,
    val hasVoted: Boolean,
    val voteId: Long,
    val userId: Long,
)
