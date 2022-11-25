package com.example.sns_project

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sns_project.databinding.CommentItemBinding
import com.example.sns_project.databinding.HomeItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.comment_item.view.*
import kotlinx.android.synthetic.main.home_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentRecyclerAdapter (private val viewModel: CommentViewModel, val commentList: MutableList<CommentDTO>) : RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>() {
    private lateinit var view: View
    private val auth: FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db: FirebaseFirestore = Firebase.firestore
    var comments = mutableListOf<CommentDTO>()
    private val mbinding: CommentItemBinding? = null
    private val binding get() = mbinding!!
    val currentUserEmail = auth.currentUser?.email.toString()
    lateinit var navController: NavController

    @SuppressLint("RestrictedApi")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = CommentItemBinding.inflate(view, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { //imageView 클릭시 좋아요, 댓글 이벤트 추가하기
        holder.bind(position)
    }

    inner class ViewHolder(private val binding: CommentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val profileImg : ImageView = itemView.commentProfile
        private val user: TextView = itemView.commentId
        private val content: TextView = itemView.commentView

        fun bind(pos: Int) {
            with (viewModel.getItem(pos)) {
                displayImageRef(profile, profileImg)
                itemView.commentId.text = user
                itemView.commentView.text = content
            }
//            displayImageRef(item.profile, profileImg)
//
//            user.text = item.user
//            content.text = item.content
        }

        private fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
            imageRef!!.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(view.context)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .into(view)
            }

        }

    }
}