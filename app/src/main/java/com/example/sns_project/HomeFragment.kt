package com.example.sns_project

import android.annotation.SuppressLint
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.home_item.view.*
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) { //피드창, R.layout.fragment_home
    /*companion object {
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    } */
    private val auth : FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db : FirebaseFirestore = Firebase.firestore
    private val postCollectionReference : CollectionReference = db.collection("post")
    private lateinit var getResultImage: ActivityResultLauncher<Intent>

    private lateinit var databaseRef: DatabaseReference

    lateinit var homeRecyclerAdapter: HomeRecyclerAdapter
    val posts = mutableListOf<PostDTO>()

    /* private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { //initialization
        if(it.resultCode == Activity.RESULT_OK) {
            val imageUrl = it.data?.data
        }
    } */

    // 바인딩 객체 타입에 ?를 붙여서 null을 허용 해줘야한다. ( onDestroy 될 때 완벽하게 제거를 하기위해 )
    private var mBinding: FragmentHomeBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("hihi", "home");
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        databaseRef = FirebaseDatabase.getInstance().reference

        initRecycler()

        binding.root.button.setOnClickListener {  //친구 추가 버튼 클릭할 시 친구 목록에 보이게
            Toast.makeText(it.context, "Add friend" , Toast.LENGTH_SHORT).show()
        }

        //view?.findViewById<RecyclerView>(R.id.imageView2)?.adapter = RecyclerViewAdapter()
//        val mRecyclerView = binding.homeRecycler
//        val mRecyclerAdapter = RecyclerViewAdapter()
//        mRecyclerView.setLayoutManager(LinearLayoutManager(this.context))

        return binding.root
    }

    private fun initRecycler() {
        homeRecyclerAdapter = HomeRecyclerAdapter(this.requireContext())
        binding.root.home_recycler.adapter = homeRecyclerAdapter

        binding.root.home_recycler.addItemDecoration(DividerItemDecoration(this.context, 1))

        db.collection("post")
            .get()
            .addOnSuccessListener { result ->
                posts.clear()
                homeRecyclerAdapter.posts.clear()
                for (document in result) {
                    posts.add(PostDTO(user = "${document.data["user"]}", create_at = "${document.data["create_at"]}", content="${document.data["content"]}"))
//                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("error", "Error getting documents.", exception)
            }

            homeRecyclerAdapter.posts = posts
            homeRecyclerAdapter.notifyDataSetChanged()


        }
    }
