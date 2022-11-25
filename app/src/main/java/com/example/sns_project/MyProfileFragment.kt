package com.example.sns_project

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.sns_project.databinding.FragmentHomeBinding
import com.example.sns_project.databinding.FragmentMyprofileBinding
import com.example.sns_project.databinding.HomeItemBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_myprofile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MyProfileFragment : Fragment(R.layout.fragment_myprofile) { //내 프로필 조회
    private val auth: FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db: FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference: CollectionReference = db.collection("users")
    private val postsCollectionReference: CollectionReference = db.collection("post")
    private val storage: FirebaseStorage = Firebase.storage
    private val storageRef: StorageReference = storage.getReference()

    val currentUserEmail = auth.currentUser?.email.toString()

    fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener {
// Failed to download the image
        }
    }


    lateinit var profileRecyclerAdapter: ProfileRecyclerAdapter
    val posts = mutableListOf<PostDTO>()

    // 바인딩 객체 타입에 ?를 붙여서 null을 허용 해줘야한다. ( onDestroy 될 때 완벽하게 제거를 하기위해 )
    private var mBinding: FragmentMyprofileBinding? = null
    private var iBinding: HomeItemBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    private val ibinding get() = iBinding!!

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentMyprofileBinding.inflate(inflater, container, false)
        iBinding = HomeItemBinding.inflate(inflater, container, false)

        val profileView = binding.profileView
        val nicknameTextView = binding.nicknameTextView
        val birthTextView = binding.birthTextView
        val descriptionTextView = binding.descriptionTextView

        // 사용자 개인의 피드.

        val imageRef = storageRef.child("image/profile/${currentUserEmail}.jpg")
        displayImageRef(imageRef, profileView) //사용자 프로필 이미지 보이기.


        usersCollectionReference.document(currentUserEmail).get()
            .addOnSuccessListener {
                nicknameTextView.text = it["nickname"].toString()
                birthTextView.text = it["birth"].toString()
                if (it["description"] == null) descriptionTextView.text = "소개글이 없습니다."
                else descriptionTextView.text = it["description"].toString()
            }

        initRecycler()

        profileRecyclerAdapter = ProfileRecyclerAdapter(this.requireContext(), posts)
        binding.root.profileRecycler.adapter = profileRecyclerAdapter

        binding.root.profileRecycler.addItemDecoration(DividerItemDecoration(this.context, 1))

        return binding.root
    }

    private fun initRecycler() {
        CoroutineScope(Dispatchers.Default).launch {
            postsCollectionReference
                .get()
                .addOnSuccessListener { result ->
                    posts.clear()
                    for (document in result) {
                        if (document.data["user"].toString().equals(currentUserEmail)) {
                            val timestamp = document.data["created_at"] as Timestamp

//                            val sf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA)
//                            sf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
//                            val time = sf.format(timestamp.toDate())
                            val time = timestamp.toDate()

                            var profileRef: StorageReference =
                                storageRef.child("image/defaultImg.png");
                            var postingImg: StorageReference =
                                storageRef.child("image/defaultImg.png");

                            if (document.data["image_uri"] == null) {
                                profileRef =
                                    storageRef.child("image/profile/${document.data["user"]}.jpg")

                                posts.add(
                                    PostDTO(
                                        profile = profileRef,
                                        user = "${document.data["nickname"]}",
                                        created_at = time,
                                        content = "${document.data["content"]}",
                                    )
                                )
                            } else {
                                profileRef =
                                    storageRef.child("image/profile/${document.data["user"]}.jpg")

                                postingImg =
                                    storageRef.child("image/posting/${document.data["user"]}${timestamp.toDate()}.jpg")

                                Log.d("hihi", "user ::: ${document.data["user"]}")
                                Log.d("hihi", "time ::: ${timestamp.toDate()}")
                                posts.add(
                                    PostDTO(
                                        profile = profileRef,
                                        user = "${document.data["nickname"]}",
                                        created_at = time,
                                        content = "${document.data["content"]}",
                                        image_uri = postingImg
                                    )
                                )
                            }
                            posts.sortByDescending { it.created_at }
                            profileRecyclerAdapter!!.posts = posts
                            profileRecyclerAdapter!!.notifyDataSetChanged()
                        }

                    }


                }
        }
    }
}

