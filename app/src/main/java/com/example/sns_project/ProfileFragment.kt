package com.example.sns_project

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.sns_project.databinding.FragmentProfileBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.log


class ProfileFragment: Fragment() { //프로필 수정창
    private val auth : FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db : FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference : CollectionReference = db.collection("users")
    private val storage: FirebaseStorage = Firebase.storage
    private val storageRef : StorageReference = storage.getReference()
    val currentUserEmail = auth.currentUser?.email.toString()
    private var imgDataUri : Uri ?= null
    private lateinit var bitmap : Bitmap
    private lateinit var getResultImage: ActivityResultLauncher<Intent>

    private val PERMISSION_Album = 101
    private val REQUEST_STORAGE = 1000

    override fun getContext(): Context? {
        return super.getContext()
    }

    // 바인딩 객체 타입에 ?를 붙여서 null을 허용 해줘야한다. ( onDestroy 될 때 완벽하게 제거를 하기위해 )
    private var mBinding: FragmentProfileBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener {
// Failed to download the image
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 액티비티 와는 다르게 layoutInflater 를 쓰지 않고 inflater 인자를 가져와 뷰와 연결한다.
        mBinding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.selectProfileButton.setOnClickListener {
            loadImage()
        }

        val imageRef =storageRef.child("image/profile/${currentUserEmail}.jpg")
        displayImageRef(imageRef, binding.profileImgView) //사용자 프로필 이미지 보이기.

        getResultImage = registerForActivityResult( //갤러리에서 이미지를 가져오게 하기
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                imgDataUri = result.data?.data!!

                try {
                    bitmap =
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, imgDataUri)
                    profileImgView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.saveProfileButton.setOnClickListener {
            var isCorrectUser : Boolean = true
            var isCorrectBirth : Boolean = true

            val editUserName = binding.editUsername.text.toString()
            val editBirth = binding.editBirth.text.toString()
            val editDescription = binding.editDescription.text.toString()

            if(editUserName != "" ){
                isCorrectUser = checkUserInfo(editUserName)
            }
            if(editBirth != ""){
                isCorrectBirth = checkBirthInfo(editBirth)
            }

            if(isCorrectUser && isCorrectBirth) {
                editUserInfo(editUserName, editBirth, editDescription)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->
                profileImgView.setImageURI(uri)
            }
        }
    }

    //중복된 사용자인가?
    private fun checkUserInfo(editUsername : String) : Boolean{
        var isCorrect : Boolean = true
        CoroutineScope(Dispatchers.Default).launch {
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if(document.data["nickname"].toString().equals(editUsername)) isCorrect = false
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("error", "Error getting documents.", exception)
                }
        }
        return isCorrect;
    }

    private fun checkBirthInfo( editBirth : String) : Boolean{
        val birthPattern = "^((19|20)\\d\\d)?([-/.])?(0[1-9]|1[012])([-/.])?(0[1-9]|[12][0-9]|3[01])$" //YYYYMMDD
        //중복된 사용자인가?

        if(!Pattern.matches(birthPattern, editBirth)) { //생년월일 정규식 확인
            Toast.makeText(
                context,"올바른 생년월일 입력 형식이 아닙니다.",
                Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        return true
    }


    private fun editUserInfo(editUsername : String, editBirth : String, editDescription: String) {
//        val editUserData = hashMapOf(
//            "nickname" to editUsername,
//            "email" to email,
//            "birth" to editBirth
//        )

        if(imgDataUri != null){
            var storageRef = storage.reference
            var postingImg = storageRef.child("image/profile/${currentUserEmail}.jpg")
            postingImg.delete()
            var savePostingImg = postingImg.putFile(imgDataUri!!)

            usersCollectionReference.document(currentUserEmail).update("profileImg", postingImg.toString())
        }

        usersCollectionReference.document(currentUserEmail).update("nickname", editUsername)
        usersCollectionReference.document(currentUserEmail).update("birth", editBirth)
        usersCollectionReference.document(currentUserEmail).update("description", editDescription)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "프로필 수정 완료!",
                    Toast.LENGTH_LONG
                )
                    .show()
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "프로필 수정에 실패 하였습니다.\n${it.message}",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
    }
}