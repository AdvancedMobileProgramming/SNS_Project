package com.example.sns_project

import com.google.firebase.firestore.ServerTimestamp
import java.sql.Timestamp
import java.util.*

//피드에 올라갈 db 저장
data class PostDTO (
    val user: String,
    val created_at: String,
    val content: String
//    val image_uri: String

)


