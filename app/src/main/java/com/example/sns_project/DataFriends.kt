package com.example.sns_project

import com.google.firebase.storage.StorageReference

data class DataFriends (
    val profile: StorageReference?= null,
    val user: String,
    val nickname: String
    )