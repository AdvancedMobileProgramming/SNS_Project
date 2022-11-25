package com.example.sns_project

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.StorageReference
import java.util.*

//피드에 올라갈 db 저장
data class CommentDTO(
    val content: String,
    val user: String,
    val profile: StorageReference ?= null,
    val time: String
)

class CommentViewModel : ViewModel() {
    val commentsLiveData : MutableLiveData<ArrayList<CommentDTO>> = MutableLiveData<ArrayList<CommentDTO>>()
    val comments  = ArrayList<CommentDTO>()

//    val itemClickEvent = MutableLiveData<Int>()

    fun addItem(com: CommentDTO) {
        comments.add(com)
        commentsLiveData.value = comments // let the observer know the livedata changed
    }
    fun updateItem(pos: Int, com: CommentDTO) {
        comments[pos] = com
        commentsLiveData.value = comments // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }
    fun deleteItem(pos: Int) {
        comments.removeAt(pos)
        commentsLiveData.value = comments
    }

    fun getItem(pos: Int) =  comments[pos]
}


