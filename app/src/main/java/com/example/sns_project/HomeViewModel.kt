package com.example.sns_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.storage.StorageReference
import java.util.*

//피드에 올라갈 db 저장
data class PostDTO (
    val profile : StorageReference ?= null,
    val user: String,
    val created_at: Timestamp,
    val content: String,
//    val image_uri: Bitmap?= null
    val image_uri: StorageReference?= null,
    val favorite: MutableMap<String, Boolean> = HashMap(),
    val favoriteCount: Int = 0
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "user" to user,
            "favoriteCount" to favoriteCount
        )
    }
}

class HomeViewModel : ViewModel() {
    val postsLiveData = MutableLiveData<ArrayList<PostDTO>>()
    val posts = ArrayList<PostDTO>()

//    val itemClickEvent = MutableLiveData<Int>()

    fun addItem(post: PostDTO) {
        posts.add(post)
        postsLiveData.value = posts // let the observer know the livedata changed
    }
    fun updateItem(pos: Int, post: PostDTO) {
        posts[pos] = post
        postsLiveData.value = posts // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }
    fun deleteItem(pos: Int) {
        posts.removeAt(pos)
        postsLiveData.value = posts
    }
}


