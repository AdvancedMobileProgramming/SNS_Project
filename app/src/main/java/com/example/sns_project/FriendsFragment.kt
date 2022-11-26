package com.example.sns_project

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.sns_project.databinding.FragmentFriendsBinding
import com.example.sns_project.databinding.FragmentFriendsBinding.inflate
import com.example.sns_project.databinding.FriendsItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_friends.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendsFragment: Fragment(R.layout.fragment_friends) { //친구리스트 조회
    private var mbinding: FragmentFriendsBinding? = null
    private val binding get() = mbinding!!
    private var ibinding : FriendsItemBinding? = null

    private var auth: FirebaseAuth? = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    private val currentUserEmail : String = auth?.currentUser?.email.toString()
    private lateinit var databaseRef: DatabaseReference
    private val storage: FirebaseStorage = Firebase.storage
    private val storageRef : StorageReference = storage.getReference()

    lateinit var friendsListAdapter: FriendsListAdapter
    val datafriends = mutableListOf<DataFriends>()

    //뷰가 생성되었을 때
    //프래그먼트와 레이아웃을 연결시켜주는 부분
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mbinding = inflate(inflater, container, false)
        databaseRef = FirebaseDatabase.getInstance().reference

        initFriendRecycler()


        friendsListAdapter = FriendsListAdapter(this.requireContext())
        binding.root.friends_recycler.adapter = friendsListAdapter

        binding.root.friends_recycler.addItemDecoration(DividerItemDecoration(this.context, 1))

        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initFriendRecycler() {

        var profileRef: StorageReference = storageRef.child("image/defaultImg.png");

        CoroutineScope(Dispatchers.Default).launch {
            Log.d("hahaha", "$currentUserEmail")
            db.collection("users").document("$currentUserEmail")
                .collection("friends")//friend 컬렉션에서
                .get()                           //데이터 가져옴
                .addOnSuccessListener { result ->
                    datafriends.clear()
                    for (document in result) {
                        Log.d("hahaha", "${document.data["nickname"]}")
                        //프로필 이미지는 스토리지에서 가져옴
                        profileRef = storageRef.child("image/profile/${document.data["user"]}.jpg")
                        datafriends.add(   //데이터 클래스에 데이터 추가
                            DataFriends(
                                profile = profileRef,   //friend 컬렉션의 "profile" 데이터를 데이터클래스의 profile에 넣기
                                user = "${document.data["user"]}",  //friend 컬렉션의 "user" 데이터를 데이터클래스의 user에 넣기
                                nickname = "${document.data["nickname"]}"  //friend 컬렉션의 "nickname" 데이터들을 데이터클래스의 nickname에 넣기
                            )
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
