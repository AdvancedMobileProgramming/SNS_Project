package com.example.sns_project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import java.text.SimpleDateFormat
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

        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        databaseRef = FirebaseDatabase.getInstance().reference

        //mBinding!!.root.button.setOnClickListener {  //친구 추가 버튼 클릭할 시 친구 목록에 보이게
        //}

        //view?.findViewById<RecyclerView>(R.id.imageView2)?.adapter = RecyclerViewAdapter()

        return binding.root
    }



    /*data class PostInfo (
        var content: String? = null,
        var creat_at: String? = null,
        var image_uri: String? = null,
        var user: String? = null
    )*/

    inner class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

        private var mpostInfo = arrayListOf<PostDTO>()
        init {
            val fireStore = FirebaseFirestore.getInstance()
            val fire = FirebaseAuth.getInstance()
            fireStore.collection("post").get().addOnSuccessListener { result ->
                for (snapshot in result) {
                    if (snapshot["user"].toString() == auth.uid) {
                        Log.d("user Success", "user Success")
                        mpostInfo.add(snapshot.toObject(PostDTO::class.java))
                    }
                }
                //binding.postCount.text = postInfo1.size.toString() + "개"
                notifyDataSetChanged()
            }
        }

        inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val profileImage = itemView.findViewById<ImageView>(R.id.imageView2)
            val profileContents = itemView.findViewById<EditText>(R.id.editTextTextMultiLine2)
            val profileCreate = itemView.findViewById<TextView>(R.id.textView3)
            val profileUser = itemView.findViewById<TextView>(R.id.textView2)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            Log.d("onCreateView", "INHOLDERcreate")
            val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_home, parent,false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            Log.d("viewHolder", "ViewHold")
            Glide.with(holder.itemView.context) //firebase에서 사진 받아와서 피드에뜨게,,
                .load(mpostInfo[position].image_uri)
                .circleCrop()
                .into(holder.profileImage)

            //holder.profileContents.text = postInfo1[position].content
            //Glide.with(holder.itemView.context).load(postInfo1.image_uri).into(holder.profileImage) //profileImage
            //Glide.with(holder.itemView.context).load(mpostInfo[position].content).into(holder.profileContents) //작성한 글
            //Glide.with(holder.itemView.context).load(mpostInfo[position].content).into(holder.profileCreate)
            //Glide.with(holder.itemView.context).load(mpostInfo[position].content).into(holder.profileUser)
        }

        override fun getItemCount(): Int {
            Log.d("COUNT", mpostInfo.size.toString())
            return mpostInfo.size
        }
    }
}