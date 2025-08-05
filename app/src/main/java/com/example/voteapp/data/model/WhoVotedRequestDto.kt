package com.example.voteapp.data.model

data class WhoVotedYetRequestDto(
    val voteId: Long,
    val alreadyVoted: Boolean
)

data class WhoVotedYetResponseDto(
    val id: Long,
    val userId: Long,
    val voteId: Long,
    val alreadyVoted: Boolean
)
