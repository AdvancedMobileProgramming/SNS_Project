package com.example.sns_project

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract.Attendees.query
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sns_project.databinding.FragmentHomeBinding
import com.google.firebase.Timestamp
import com.example.sns_project.databinding.HomeItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.home_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.typeOf


class HomeFragment : Fragment(R.layout.fragment_home) { //피드창, R.layout.fragment_home
    /*companion object {
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    } */
    private val auth: FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db: FirebaseFirestore = Firebase.firestore
    private val postCollectionReference: CollectionReference = db.collection("post")
    private lateinit var getResultImage: ActivityResultLauncher<Intent>

    private lateinit var imgDataUri : Uri
    private lateinit var bitmap : Bitmap

    private lateinit var databaseRef: DatabaseReference
    private val storage: FirebaseStorage = Firebase.storage
    private val storageRef : StorageReference = storage.getReference()
    val currentUserEmail = auth.currentUser?.email.toString()

    lateinit var homeRecyclerAdapter: HomeRecyclerAdapter
    val posts = mutableListOf<PostDTO>()
    val friends = mutableListOf<DataFriends>()

    /* private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { //initialization
        if(it.resultCode == Activity.RESULT_OK) {
            val imageUrl = it.data?.data
        }
    } */

    // 바인딩 객체 타입에 ?를 붙여서 null을 허용 해줘야한다. ( onDestroy 될 때 완벽하게 제거를 하기위해 )
    private var mBinding: FragmentHomeBinding? = null
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

        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        iBinding = HomeItemBinding.inflate(inflater, container, false)
        databaseRef = FirebaseDatabase.getInstance().reference

        initRecycler()

        homeRecyclerAdapter = HomeRecyclerAdapter(this.requireContext(), posts)
        binding.root.home_recycler.adapter = homeRecyclerAdapter

        binding.root.home_recycler.addItemDecoration(DividerItemDecoration(this.context, 1))

        FriendAdd()

        return binding.root
    }

     private fun initRecycler() {
         CoroutineScope(Dispatchers.Default).launch {
             db.collection("post")
                 .get()
                 .addOnSuccessListener { result ->
                     posts.clear()
                     for (document in result) {
                         val timestamp = document.data["created_at"] as Timestamp

                         val sf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA)
                         sf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
                         val time = sf.format(timestamp.toDate())

                         if(document.data["image_uri"]==null){
                             val profileRef = storageRef.child("image/profile/${document.data["user"]}.jpg")

                             posts.add(
                                 PostDTO(
                                     profile = profileRef,
                                     user = "${document.data["user"]}",
                                     created_at = "${time.toString()}",
                                     content = "${document.data["content"]}",
                                 )
                             )
                         }

                         else{
                             val profileRef = storageRef.child("image/profile/${document.data["user"]}.jpg")

                             var postingImg = storageRef.child("image/posting/${document.data["user"]}${timestamp.toDate()}.jpg")

                             posts.add(
                                 PostDTO(
                                     profile = profileRef,
                                     user = "${document.data["user"]}",
                                     created_at = "${time.toString()}",
                                     content = "${document.data["content"]}",
                                     image_uri = postingImg
                                 )
                             )
                         }
                     }

                     posts.sortByDescending{it.created_at}
                     homeRecyclerAdapter!!.posts = posts
                     homeRecyclerAdapter!!.notifyDataSetChanged()
                 }
                 .addOnFailureListener { exception ->
                     Log.d("error", "error")
                     Log.d("error", "error")
                 }
         }
     }


    private fun FriendAdd() {
        ibinding.addfriendbtn.setOnClickListener {  //친구 추가 버튼 클릭할 시 친구 목록에 보이게
            val id = ibinding.idView.text

            val friendID = hashMapOf(
                "user" to id.toString()
            )

            db.collection("friend").document("user")
                .set(friendID)
                .addOnSuccessListener {
                    Toast.makeText(context, "Add Friend Succeed", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener() {
                    Toast.makeText(context, "Add Friend Failure", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
