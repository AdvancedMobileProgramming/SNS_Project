package com.example.sns_project

import android.Manifest
import android.R
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sns_project.databinding.FragmentPostingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_posting.*
import java.net.URI
import java.util.*


class PostingFragment: Fragment() { //게시물 포스팅 창 R.layout.fragment_posting

    val PERMISSION_Album = 101
    val REQUEST_STORAGE = 1000

    private var imageURL: String? = null
    private val auth : FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db : FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference : CollectionReference = db.collection("post")
    private val storage : FirebaseStorage = Firebase.storage
    private lateinit var getResultImage: ActivityResultLauncher<Intent>
    private lateinit var bitmap : Bitmap
    private lateinit var imgDataUri : Uri

    // 바인딩 객체 타입에 ?를 붙여서 null을 허용 해줘야한다. ( onDestroy 될 때 완벽하게 제거를 하기위해 )
    private var mBinding: FragmentPostingBinding? = null

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
        mBinding = FragmentPostingBinding.inflate(inflater, container, false)


        binding.postingButton.setOnClickListener {
            val editTextTextMultiLine = binding.editTextTextMultiLine.text.toString()

            if(editTextTextMultiLine == "") { //포스팅란에 빈칸 입력시
                Toast.makeText(
                    context, "Please Fill the all blanks",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //HomeFragment 피드창으로 사진과 글이 넘어가는 코드 구현,, 못함 ㅠ
            this.uploadPost(
                binding.editTextTextMultiLine.text.toString(),
            )
        }

        binding.imageButton.setOnClickListener { //이미지버튼에 겔러리에 가서 사진 받아오기
            loadImage()
        }

        getResultImage = registerForActivityResult( //갤러리에서 이미지를 가져오게 하기
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                imgDataUri = result.data?.data!!

                try {
                    bitmap =
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, imgDataUri)
                    binding.imageButton.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

    fun loadImage() {
        var intent_image = Intent()
        intent_image.type = "image/*"
        intent_image.action = Intent.ACTION_GET_CONTENT
        getResultImage.launch(intent_image)
    }

    fun uploadPost(content: String) {
        if(binding.imageButton.drawable != null && this.imageURL == null) { //바꿔야함
            Toast.makeText(
                context,
                "이미지 업로드중..",
                Toast.LENGTH_SHORT
            ).show()
        }

        //사진 uri및 게시물 정보 저장
        val data = hashMapOf(
            //"title" to title,
            "content" to content,
            "user" to FirebaseAuth.getInstance().uid, // 현재 로그인 된 유저 정보를 업로드(?)
            "created_at" to Date(),
            "image_uri" to imgDataUri
        )

        //게시물 이미지 정보(uri) storage에 저장.
        var storageRef = storage.reference
        var postingImg = storageRef.child("image/posting" + "${FirebaseAuth.getInstance().uid}" + "${Date()}")
        var savePostingImg = postingImg.putFile(imgDataUri)

        Log.d("puuuuu", "${data}");
        db.collection("post")
            .add(data)
            .addOnCompleteListener {
                Toast.makeText(
                    context,
                    "업로드 완료!",
                    Toast.LENGTH_LONG
                )
                    .show()
                //finish() // 업로드가 성공한다면 이 화면을 종료하고 메인 페이지로 돌아감.
            }
            .addOnFailureListener {
                Toast.makeText (
                    context,
                    "포스트 업로드에 실패 하였습니다.\n${it.message}",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
    }
}