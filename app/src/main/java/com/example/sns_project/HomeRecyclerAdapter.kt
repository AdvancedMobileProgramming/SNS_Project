package com.example.sns_project

import android.R
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.FragmentFriendsBinding
import com.example.sns_project.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.home_item.view.*


class HomeRecyclerAdapter(private val context: Context, val post: MutableList<PostDTO>) : RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>() {
    private val db : FirebaseFirestore = Firebase.firestore
    private var mbinding : FragmentHomeBinding?= null
    private val binding get() = mbinding!!
    var posts = mutableListOf<PostDTO>()
//
//    init {
//        Log.d("check!!!", "adapter init : ${posts.size}")
//        db.collection("post")
//            .get()
//            .addOnSuccessListener { result ->
//                posts.clear()
//                for (document in result) {
//                    posts.add(PostDTO(user = "${document.data["user"]}", create_at = "${document.data["create_at"]}", content="${document.data["content"]}"))
////                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w("error", "Error getting documents.", exception)
//            }
//
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("check!!!", "adapter createViewHolder : ${posts.size}")
        val view = LayoutInflater.from(parent.context).inflate(com.example.sns_project.R.layout.home_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("check!!!", "adapter bind : ${posts.size}")
        holder.bind(posts[position], context)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val user: TextView = itemView.idView
        private val create: TextView = itemView.createView
        private val content: TextView = itemView.contentView

        fun bind(item: PostDTO, context :Context) {
            user.text = item.user
//            create.text = item.create_at
            content.text = item.content
        }
    }
}