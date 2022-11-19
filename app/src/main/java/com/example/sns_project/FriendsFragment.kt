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
import com.example.sns_project.databinding.FragmentFriendsBinding
import com.example.sns_project.databinding.FragmentFriendsBinding.inflate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_friends.view.*

data class DataFriends(
    var name: String,
    var profileImageURL: Int?,
    var id: String
)

class FriendsFragment: Fragment() { //친구리스트 조회
    private var binding : FragmentFriendsBinding?= null


    //뷰가 생성되었을 때
    //프래그먼트와 레이아웃을 연결시켜주는 부분
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recyclerView = binding!!.root.findViewById<RecyclerView>(R.id.friends_recycler)
        binding = inflate(inflater, container, false)
        val adapter = FriendsListAdapter()

        //test
        //val friends : ArrayList<DataFriends> = ArrayList()
        //val item = DataFriends("jang", null, "sunho1234")  //Data class에 데이터 임의로 추가
        //val item2 = DataFriends("dkgd", null, "djfdfgdgfg")
        //friends.add(item)
        //friends.add(item2)
        //adapter.FriendList(friends)
        //recyclerView.adapter = adapter
        //recyclerView.setHasFixedSize(true)

        //
        return binding!!.root
    }
}