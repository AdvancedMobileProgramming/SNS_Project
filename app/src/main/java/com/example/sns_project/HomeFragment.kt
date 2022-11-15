package com.example.sns_project

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sns_project.databinding.FragmentHomeBinding
import com.example.sns_project.databinding.FragmentPostingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class HomeFragment : Fragment() { //피드창, R.layout.fragment_home

    val PERMISSION_Album = 101
    val REQUEST_STORAGE = 1000

    private val auth : FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db : FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference : CollectionReference = db.collection("users")
    private lateinit var getResultImage: ActivityResultLauncher<Intent>

    private lateinit var databaseRef: DatabaseReference


    // 바인딩 객체 타입에 ?를 붙여서 null을 허용 해줘야한다. ( onDestroy 될 때 완벽하게 제거를 하기위해 )
    private var mBinding: FragmentHomeBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { //initialization
        if(it.resultCode == Activity.RESULT_OK) {
            val imageUrl = it.data?.data

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        databaseRef = FirebaseDatabase.getInstance().reference


        return binding.root
    }

    inner class Post {
        var content: String? = null
        var create_at: String? = null
        var image_uri: String? = null
        var user: String? = null
    }


    inner class HomeFragmentRecyclerAdapter: RecyclerView.Adapter<HomeFragmentRecyclerAdapter.ViewHolder>() {

        private var Post = arrayListOf<Post>()
        init {
            val fireStore = FirebaseFirestore.getInstance()
            fireStore.collection("posts").get().addOnSuccessListener { result ->
                for (snapshot in result) {
                    if (snapshot["uid"].toString() == auth.uid) {
                        //Post.add(HomeFragment) 에러남
                    }
                }

                //binding.postCount.text = Post.size.toString() + "개"
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_list_item, parent) //R.layout.home_item, parent, false
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val width = resources.displayMetrics.widthPixels / 3
            holder.profileImage.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            Glide.with(holder.itemView.context).load(Post[position].image_uri).into(holder.profileImage)
            //binding.postCount.text = postDto.size.toString() + "개"
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val profileImage = itemView.findViewById<ImageView>(R.id.home) //에러남
        }

        override fun getItemCount(): Int {
            return Post.size
        }
    }

}


