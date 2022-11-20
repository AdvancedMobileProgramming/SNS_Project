package com.example.sns_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.FriendsListAdapter.ViewHolder
import kotlinx.android.synthetic.main.friends_item.view.*
import kotlin.collections.ArrayList

class FriendsListAdapter(private var friends: ArrayList<DataFriends>) :
    RecyclerView.Adapter<ViewHolder>() {

    //viewHolder를 새로 만들어야 할 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.friends_item, parent, false)
        return ViewHolder(v)
    }

    //데이터 목록 표시
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(listener: View.OnClickListener, friends: DataFriends) {
            itemView.textView4.text = friends.id
            itemView.textView6.text = friends.description
            itemView.setOnClickListener(listener)
        }
    }

    //ViewHolder를 데이터와 연결할 때 호출
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friends = friends[position]
        val listener = View.OnClickListener {
            Toast.makeText(it.context, "Clicked " +
                    friends.id, Toast.LENGTH_SHORT).show()
        }
        holder.apply {
            bind(listener, friends)
            itemView.tag = friends
        }
    }

    //데이터 세트 크기를 가져올 때 호출
    override fun getItemCount(): Int {
        return friends.size
    }
}

