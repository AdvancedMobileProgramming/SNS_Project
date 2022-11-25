package com.example.sns_project

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.HomeItemBinding
import com.google.firebase.auth.ktx.auth
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sns_project.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.home_item.*
import kotlinx.android.synthetic.main.home_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeRecyclerAdapter(private val context: Context, val postList: MutableList<PostDTO>) : RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>() {
    private val db : FirebaseFirestore = Firebase.firestore
    var posts = mutableListOf<PostDTO>()
    private val mbinding : HomeItemBinding?= null
    private val binding get() = mbinding!!
    private val auth : FirebaseAuth = Firebase.auth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = HomeItemBinding.inflate(view, parent, false)
        val Usernickname = Firebase.auth.currentUser?.uid.toString()


        var isDefault = true
        var imageCount = 0
        binding.imageView2.setOnClickListener {
            Log.d("add", "clicked!")

            //binding.imageView2.setImageResource(R.drawable.favorite_click)

            isDefault=!isDefault
            if(isDefault) {
                binding.imageView2.setImageResource(R.drawable.favorite_click)
                binding.TextView6.text = "like: " + imageCount
                imageCount--
            }

            else {
                binding.imageView2.setImageResource(R.drawable.favorite_border)
                binding.TextView6.text = "like: " + imageCount
                imageCount++
            }
        }


        binding.addfriendbtn.setOnClickListener {

            Log.d("add", "buttonClick")

            CoroutineScope(Dispatchers.Default).launch {
                    db.collection("users")
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                val friendID = hashMapOf(
                                    "profile" to document.data["profileImg"].toString(),
                                    "user" to document.data["email"].toString(),
                                    "nickname" to document.data["nickname"].toString()
                                )

                                // friend 컬렉션의 nickname과 "users"의 nickname이 같으면 데이터 추가 X -> 구현해야함

                                if (document.data["nickname"].toString()
                                        .equals(binding.idView.text.toString()))
                                {
                                    Toast.makeText(context, "친구 추가 중", Toast.LENGTH_SHORT)
                                        .show()
                                    db.collection("friend")
                                        .add(friendID)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "친구 추가 완료", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "친구 추가 실패", Toast.LENGTH_SHORT).show()
                                        }
                                }


                            }
                        }
                }
            }
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = posts.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) { //imageView 클릭시 좋아요, 댓글 이벤트 추가하기
        holder.bind(posts[position])
        /*holder.itemView.imageView2.setOnClickListener {
            favoriteEvent(position)
        }*/
    }

    inner class ViewHolder(private val binding : HomeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val profileImg : ImageView = itemView.postingProfile
        private val user: TextView = itemView.idView
        private val create: TextView = itemView.createView
        private val content: TextView = itemView.contentView
        private val image: ImageView = itemView.postingImgView
        //private val favorite: ImageView = itemView.imageView2 //좋아요 imageView

        fun bind(item: PostDTO) {
            displayImageRef(item.profile, profileImg)
                if (item.image_uri == null) {
                    itemView.postingImgView.visibility = View.GONE
                } else {
                    CoroutineScope(Dispatchers.Default).launch {
                        displayImageRef(item.image_uri, image)
                    }
                }

            val sf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA)
             sf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
             val time = sf.format(item.created_at)

            user.text = item.user
            create.text = time
            content.text = item.content

            binding.imageView4.setOnClickListener{
                lateinit var navController: NavController
                navController = Navigation.findNavController(binding.root)
                Log.d("haha", "${item.user}${item.created_at}")
                val bundle = bundleOf("post" to "${item.user}${item.created_at}")
                navController.navigate(com.example.sns_project.R.id.action_homeFragment_to_commentFragment, bundle)
            }
        }

        private fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
            imageRef!!.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(view.context)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .into(view)
            }
//            imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener{
//                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
//                view.setImageBitmap(bmp)
//            }?.addOnFailureListener {
//            }
        }

        private fun displayPostingImageRef(bmp : Bitmap?, view: ImageView) {
            view.setImageBitmap(bmp)
        }
    }
}