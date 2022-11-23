package com.example.sns_project

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.sns_project.databinding.FragmentFriendsBinding
import com.example.sns_project.databinding.FragmentFriendsBinding.inflate
import com.example.sns_project.databinding.FriendsItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_friends.view.*
import kotlinx.android.synthetic.main.fragment_myprofile.*
import kotlinx.android.synthetic.main.friends_item.*
import kotlinx.android.synthetic.main.home_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendsFragment: Fragment(R.layout.fragment_friends) { //친구리스트 조회
    private var mbinding: FragmentFriendsBinding? = null
    private val binding get() = mbinding!!

    private val db: FirebaseFirestore = Firebase.firestore
    private lateinit var databaseRef: DatabaseReference

    lateinit var friendsListAdapter: FriendsListAdapter
    val datafriends = mutableListOf<DataFriends>()

    //뷰가 생성되었을 때
    //프래그먼트와 레이아웃을 연결시켜주는 부분
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("view", "friend")

        mbinding = inflate(inflater, container, false)
        databaseRef = FirebaseDatabase.getInstance().reference

        initFriendRecycler()

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initFriendRecycler() {
        friendsListAdapter = FriendsListAdapter(this.requireContext())
        binding.root.friends_recycler.adapter = friendsListAdapter

        binding.root.friends_recycler.addItemDecoration(DividerItemDecoration(this.context, 1))

        Log.d("view", "friendadd")
        CoroutineScope(Dispatchers.Default).launch {
            db.collection("friend")
                .get()
                .addOnSuccessListener { result ->
                    datafriends.clear()
                    friendsListAdapter.datafriends.clear()
                    for (document in result) {
                            datafriends.add(
                                DataFriends(nickname = "${document.data["user"]}",
                                    description = "${document.data["description"]}")
                            )
                    }
                    friendsListAdapter.notifyDataSetChanged()
                    friendsListAdapter.datafriends = datafriends
                }
                .addOnFailureListener { exception ->
                    Log.w("error", "Error getting documents", exception)
                }
        }
    }
}