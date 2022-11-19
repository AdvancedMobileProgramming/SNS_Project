package com.example.sns_project

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.R.drawable
import com.example.sns_project.databinding.FragmentFriendsBinding
import com.example.sns_project.databinding.FragmentFriendsBinding.inflate

data class DataFriends(
    var id: String,
    var profileImageURL: Drawable?,
    var description: String
)

class FriendsFragment: Fragment() { //친구리스트 조회
    private var mbinding : FragmentFriendsBinding?= null
    private val binding get() = mbinding!!

    //뷰가 생성되었을 때
    //프래그먼트와 레이아웃을 연결시켜주는 부분
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mbinding = inflate(inflater, container, false)
        val recyclerView = this.binding.root.findViewById<RecyclerView>(R.id.friends_recycler)
        recyclerView.addItemDecoration(DividerItemDecoration(this.context, 1))

        //test 데이터 직접 넣기
        val friends : ArrayList<DataFriends> = ArrayList()
        friends.add(DataFriends("jang",
            context?.let { ContextCompat.getDrawable(it, drawable.logo) },"Hello"))
        friends.add(DataFriends("dkgd", context?.let { ContextCompat.getDrawable(it, drawable.img) }, "djfdfgdgfg"))

        val adapter = FriendsListAdapter(friends)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }
}