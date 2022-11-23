package com.example.sns_project

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_signin.view.*
import kotlinx.android.synthetic.main.home_item.view.*


class HomeRecyclerAdapter(private val context: Context, val post: MutableList<PostDTO>) : RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>() {
    private val db : FirebaseFirestore = Firebase.firestore
    private var mbinding : FragmentHomeBinding?= null
    private val binding get() = mbinding!!
    var posts = mutableListOf<PostDTO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(com.example.sns_project.R.layout.home_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { //imageView 클릭시 좋아요, 댓글 이벤트 추가하기
        holder.bind(posts[position], context)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val profileImg : ImageView = itemView.postingProfile
        private val user: TextView = itemView.idView
        private val create: TextView = itemView.createView
        private val content: TextView = itemView.contentView
        private val image: ImageView = itemView.postingImgView


        fun bind(item: PostDTO, context :Context) {
            if(item.profile == null){
                itemView.postingProfile.visibility = View.GONE
            }else{
                displayImageRef(item.profile, profileImg)
            }

            if(item.image_uri == null){
                itemView.postingImgView.visibility = View.GONE
            }else{
                displayImageRef(item.image_uri, image)
            }


            user.text = item.user
            create.text = item.created_at
            content.text = item.content
        }

        fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
            imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                view.setImageBitmap(bmp)
            }?.addOnFailureListener {
            }
        }
    }
}