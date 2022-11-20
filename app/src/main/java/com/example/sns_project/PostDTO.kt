package com.example.sns_project

data class PostDTO ( //피드에 올라갈 db 저장
    var content: String? = null,
    var creat_at: String? = null,
    var image_uri: String? = null,
    var user: String? = null
)