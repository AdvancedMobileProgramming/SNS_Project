package com.example.sns_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.FragmentFriendsBinding
import kotlinx.android.synthetic.main.friends_item.view.*

class FriendsListAdapter(private var context: Context) :
    RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    var datafriends = mutableListOf<DataFriends>()

    //viewHolder를 새로 만들어야 할 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friends_item, parent, false)
        return ViewHolder(view)
    }

    //데이터 목록 표시
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val user: TextView = itemView.textView4

        fun bind(item: DataFriends) {
            user.text = item.id

            /*user.setOnClickListener {
                Toast.makeText(it.context, "ID : ${item.id}", Toast.LENGTH_SHORT).show()
            }*/
        }
    }

    //ViewHolder를 데이터와 연결할 때 호출
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datafriends[position])
    }

    //데이터 세트 크기를 가져올 때 호출
    override fun getItemCount(): Int {
        return datafriends.size
    }
}

