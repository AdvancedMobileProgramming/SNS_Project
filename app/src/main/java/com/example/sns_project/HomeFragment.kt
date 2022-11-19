package com.example.sns_project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
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
import kotlinx.android.synthetic.main.home_item.view.*


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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        databaseRef = FirebaseDatabase.getInstance().reference

        //mBinding!!.root.button.setOnClickListener {  //친구 추가 버튼 클릭할 시 친구 목록에 보이게
        //}

        return binding.root
    }

    /*inner class PostInfo {
        var content: String? = null
        var create_at: String? = null
        var image_uri: String? = null
        var user: String? = null
    }*/

    data class PostInfo(
        var content: String? = null,
        var creat_at: String? = null,
        var image_uri: String? = null,
        var user: String? = null
    )

    inner class ProfileFragmentRecyclerAdapter: RecyclerView.Adapter<ProfileFragmentRecyclerAdapter.ViewHolder>() {

        private var postInfo = arrayListOf<PostInfo>()
        init {
            val fireStore = FirebaseFirestore.getInstance()
            fireStore.collection("posts").get().addOnSuccessListener { result ->
                for (snapshot in result) {
                    if (snapshot["users"].toString() == auth.uid) {
                        postInfo.add(snapshot.toObject(PostInfo::class.java))
                    }
                }

                //binding.postCount.text = postInfo.size.toString() + "개"
                notifyDataSetChanged()
            }
        }

        //@SuppressLint("ResourceType")
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //val width = resources.displayMetrics.widthPixels / 3
            //holder.profileImage.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            //binding.postCount.text = postInfo.size.toString() + "개"
            Glide.with(holder.itemView.context).load(postInfo[position].image_uri).into(holder.profileImage) //profileImage
            //Glide.with(holder.itemView.context).load(postInfo[position].content).into(holder.profileContents) //작성한 글
            //Glide.with(holder.itemView.context).load(postInfo[position].create_at).into(holder.profileCreate)
            //Glide.with(holder.itemView.context).load(postInfo[position].user).into(holder.profileUser)
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val profileImage = itemView.findViewById<ImageView>(R.id.imageView2)
            var profileContents = itemView.findViewById<EditText>(R.id.editTextTextMultiLine2)
            val profileCreate = itemView.findViewById<TextView>(R.id.textView3)
            val profileUser = itemView.findViewById<TextView>(R.id.textView2)
        }

        override fun getItemCount(): Int { //피드개수 count할 메서드(안씀)
            return postInfo.size
        }
    }
}


