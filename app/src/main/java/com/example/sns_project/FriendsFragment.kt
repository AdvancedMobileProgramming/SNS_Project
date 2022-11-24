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

    private val db: FirebaseFirestore = Firebase.firestore
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
        Log.d("view", "friend")

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

        Log.d("view", "friendadd")
        CoroutineScope(Dispatchers.Default).launch {
            db.collection("friend")
                .get()
                .addOnSuccessListener { result ->
                    datafriends.clear()
                    for (document in result) {
                        profileRef = storageRef.child("image/profile/${document.data["user"]}.jpg")
                        datafriends.add(
                            DataFriends(
                                profile = profileRef,
                                user = "${document.data["user"]}",
                                nickname = "${document.data["nickname"]}"
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
