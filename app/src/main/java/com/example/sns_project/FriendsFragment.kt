package com.example.sns_project

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

data class Friends(
    var name: String?= null,
    var profileImageUrl: String?= null,
    var id: String?= null
)

class FriendsFragment: Fragment(R.layout.fragment_friends) { //친구리스트 조회
    private lateinit var database: DatabaseReference
    private var friends: ArrayList<Friends> = arrayListOf()

    //뷰가 생성되었을 때
    //프래그먼트와 레이아웃을 연결시켜주는 부분
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = Firebase.database.reference
        val view = inflater.inflate(R.layout.fragment_friends, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.friends_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecyclerViewAdapter()

        return view
    }

    //친구들의 정보들을 담아오는 부분
    inner class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>(){
        init {
            val myUid = Firebase.auth.currentUser?.uid.toString()
            val addValueEventListener = FirebaseDatabase.getInstance().reference.child("users")
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        friends.clear()
                        for (data in snapshot.children) {
                            val item = data.getValue<Friends>()
                            if (item?.id.equals(myUid)) {
                                continue
                            } // 본인은 친구창에서 제외
                            friends.add(item!!)
                        }
                        notifyDataSetChanged()
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_friends, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imageView3)
            val nametv : TextView = itemView.findViewById(R.id.textView4)
            val idtv : TextView = itemView.findViewById(R.id.textView6)
        }

        //Glide를 이용해 프로필 띄우고, 이름과 아이디 구현
        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            Glide.with(holder.itemView.context).load(friends[position].profileImageUrl)
                .circleCrop()
                .into(holder.imageView)
            holder.nametv.text = friends[position].name
            holder.idtv.text = friends[position].id
            }

        override fun getItemCount(): Int {
            return friends.size
        }
    }
}





















