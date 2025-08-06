package com.example.voteapp.data.model

data class  OptionRequestDto (
    val name: String,
    val imageData: ByteArray? = null,
    val voteId: Long
)