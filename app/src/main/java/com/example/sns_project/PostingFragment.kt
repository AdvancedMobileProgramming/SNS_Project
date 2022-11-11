package com.example.sns_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Gallery
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.sns_project.databinding.FragmentPostingBinding
import com.example.sns_project.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_posting.*
import java.util.regex.Pattern

class PostingFragment: Fragment(R.layout.fragment_posting) { //게시물 포스팅 창 R.layout.fragment_posting

    private val auth : FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db : FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference : CollectionReference = db.collection("users")

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
        }

        binding.imageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            startForResult.launch(intent)
        }

        return binding.root
    }
}