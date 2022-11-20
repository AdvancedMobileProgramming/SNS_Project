package com.example.sns_project

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.FragmentFriendsBinding
import com.example.sns_project.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.home_item.view.*


class HomeRecyclerAdapter(private val context: Context) : RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>() {
    private var mbinding : FragmentHomeBinding?= null
    private val binding get() = mbinding!!
    var posts = mutableListOf<PostDTO>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(com.example.sns_project.R.layout.home_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val user: TextView = itemView.idView
        private val create: TextView = itemView.createView
        private val content: TextView = itemView.contentView

        fun bind(item: PostDTO) {
            user.text = item.user
            create.text = item.create_at
            content.text = item.content
        }
    }
}