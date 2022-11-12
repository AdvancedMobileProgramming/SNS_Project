package com.example.sns_project

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.sns_project.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.regex.Pattern
import kotlin.math.log


class ProfileFragment: Fragment() { //프로필 수정창
    private val auth : FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db : FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference : CollectionReference = db.collection("users")


    private val PERMISSION_Album = 101
    private val REQUEST_STORAGE = 1000

    override fun getContext(): Context? {
        return super.getContext()
    }

    // 바인딩 객체 타입에 ?를 붙여서 null을 허용 해줘야한다. ( onDestroy 될 때 완벽하게 제거를 하기위해 )
    private var mBinding: FragmentProfileBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 액티비티 와는 다르게 layoutInflater 를 쓰지 않고 inflater 인자를 가져와 뷰와 연결한다.
        mBinding = FragmentProfileBinding.inflate(inflater, container, false)

        val currentUserEmail = auth.currentUser?.email.toString()
        usersCollectionReference.document(currentUserEmail).get()
            .addOnSuccessListener { // it: DocumentSnapshot
                binding.profileImgView.setImageURI(it["profile"].toString().toUri());
            }.addOnFailureListener {
            }


        binding.selectProfileButton.setOnClickListener {
//            val contract = ActivityResultContracts.RequestPermission()
//
//            val activityResultLauncher = registerForActivityResult(contract) { isGranted ->
//                if (isGranted) {
//                    // 권한이 필요한 작업 수행
//                    openGallery()
//                }
//            }
//
//            activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            requirePermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_Album)
        }

        binding.saveProfileButton.setOnClickListener {
            val editUserName = binding.editUsername.text.toString()
            val editBirth = binding.editBirth.text.toString()
            val editDescription = binding.editDescription.text.toString()

            if(editUserName == "" || editBirth == "" || editDescription == "" ){
                Toast.makeText(
                    context, "Please Fill the all blanks",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else{
                if(checkUserInfo(editUserName,editBirth, editDescription)){ //중복된 사용자정보인지, 올바른 형식으로 입력했는지 확인
                    editUserInfo(editUserName, editBirth, editDescription)
                }
            }
        }

        return binding.root
    }

    fun requirePermissions(permissions: Array<String>, requestCode: Int) {
        Log.d("permission","권한 요청");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionGranted(requestCode)
        } else {
            // isAllPermissionsGranted : 권한이 모두 승인 되었는지 여부 저장
            // all 메서드를 사용하면 배열 속에 들어 있는 모든 값을 체크할 수 있다.
//            val isAllPermissionsGranted =
//                permissions.all { ContextCompat.checkSelfPermission(this) == PackageManager.PERMISSION_GRANTED }

//            val contract = ActivityResultContracts.RequestPermission()
//
//            registerForActivityResult(contract) { isGranted ->
//                if (isGranted) {
//                    // 권한이 필요한 작업 수행
//                    openGallery()
//                }
//            }
//            if (isAllPermissionsGranted) {
//                permissionGranted(requestCode)
//            } else {
//                // 사용자에 권한 승인 요청
//                ActivityCompat.requestPermissions(Activity(), permissions, requestCode)
//                permissionDenied(requestCode)
//            }
            openGallery()
        }
    }

    private fun permissionGranted(requestCode: Int) {
        openGallery()
    }

    private fun permissionDenied(requestCode: Int) {
        when (requestCode) {
            PERMISSION_Album -> Toast.makeText(
                getContext(),
                "저장소 권한을 승인해야 앨범에서 이미지를 불러올 수 있습니다.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_STORAGE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->
                profileImgView.setImageURI(uri)
            }
        }
    }

    private fun checkUserInfo(editUsername : String, editBirth : String, editDescriptor: String) : Boolean{
        val birthPattern = "^((19|20)\\d\\d)?([-/.])?(0[1-9]|1[012])([-/.])?(0[1-9]|[12][0-9]|3[01])$" //YYYYMMDD
        val passwordPattern = """^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^+\-=])(?=\S+$).*$""" //숫자, 영어, 특수문자의 조합(하나 이상 포함), 공백 포함 불가
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
        val email = auth.currentUser?.email.toString()
//        val editUserData = hashMapOf(
//            "nickname" to editUsername,
//            "email" to email,
//            "birth" to editBirth
//        )


        usersCollectionReference.document(email).update("nickname", editUsername)
        usersCollectionReference.document(email).update("birth", editBirth)
        usersCollectionReference.document(email).update("description", editDescription)

            .addOnSuccessListener {
                Log.d("update", "success")
            }.addOnFailureListener {}


    }

}