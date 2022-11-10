package com.example.sns_project

import android.R
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sns_project.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern


class ProfileFragment: Fragment() { //프로필 수정창
    private val auth : FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db : FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference : CollectionReference = db.collection("users")

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
        val email = "1234@test.com"
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