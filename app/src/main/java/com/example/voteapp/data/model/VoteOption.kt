package com.example.voteapp.data.model

data class VoteOption (
    val id: Long,
    val name: String,
    var count: Int,
    val imageData: String?

)