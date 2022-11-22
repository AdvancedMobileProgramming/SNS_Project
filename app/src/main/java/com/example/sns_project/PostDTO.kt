package com.example.sns_project

//피드에 올라갈 db 저장
data class PostDTO (
    val user: String,
    val create_at: String,
    val content: String,
    val image_uri: String

)