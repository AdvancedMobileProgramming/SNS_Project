package com.example.sns_project

import android.annotation.SuppressLint
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class FriendsListAdapter : RecyclerView.Adapter<ViewHolder>() {
    private var friends: ArrayList<DataFriends> = ArrayList()

    //데이터 목록 표시
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ProfileImageUrl = itemView.findViewById<ImageView>(R.id.imageView3)
        val userid = itemView.findViewById<TextView>(R.id.textView6)
        val username = itemView.findViewById<TextView>(R.id.textView4)
    }

    //init {
    //    //친구들의 정보들을 담아오는 부분
    //    val myUid = Firebase.auth.currentUser?.uid.toString()
    //    FirebaseDatabase.getInstance().reference.child("users").addValueEventListener(object :
    //        ValueEventListener {
    //        override fun onCancelled(error: DatabaseError) {
//
    //        }
//
    //        @SuppressLint("NotifyDataSetChanged")
    //        override fun onDataChange(snapshot: DataSnapshot) {
    //            friends.clear()
    //            for (data in snapshot.children) {
    //                val item = data.getValue<DataFriends>()
    //                if (item?.id.equals(myUid)) { // 본인은 친구창에서 제외
    //                    continue
    //                }
    //                friends.add(item!!)
    //            }
    //            notifyDataSetChanged()
    //        }
    //    })
    //}


    //viewHolder를 새로 만들어야 할 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.friends_item, parent, false) as RecyclerView
        )
    }

    //데이터 세트 크기를 가져올 때 호출
    override fun getItemCount(): Int {
        return friends.size
    }

    //ViewHolder를 데이터와 연결할 때 호출
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int) {

    }
}
