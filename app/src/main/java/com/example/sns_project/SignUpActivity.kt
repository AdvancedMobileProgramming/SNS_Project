package com.example.sns_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.d
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.logging.Logger
import java.util.regex.Pattern
import java.util.stream.Collector


data class userInfo (
    var nickname :String? = null,
    var email : String? = null,
    var birth : String? = null,
    var password : String? = null
)

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {
//    val profileImgView = findViewById<ImageView>(R.id.profileImgView) as ImageView
//    private val checkPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
//        result.forEach {
//            if(!it.value) {
//                Toast.makeText(applicationContext, "권한 동의 필요!", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//    }
//    private val readImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        profileImgView.load(uri);
//    }


    private val auth : FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db : FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference : CollectionReference = db.collection("users")

    private val PERMISSION_Album = 101
    private val REQUEST_STORAGE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        val profileSelectButton = findViewById<Button>(R.id.selectProfileButton);
        profileSelectButton.setOnClickListener {
            requirePermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_Album)
        }

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val signUpNickname = findViewById<EditText>(R.id.signUpNickname).text.toString()
            val signUpEmail = findViewById<EditText>(R.id.signUpEmail).text.toString()
            val signUpBirth = findViewById<EditText>(R.id.signUpBirth).text.toString()
            val signUpPw = findViewById<EditText>(R.id.signUpPw).text.toString()
            val signUpCheckPw = findViewById<EditText>(R.id.signUpPwCheck).text.toString()


            if(signUpNickname == "" || signUpEmail == "" || signUpBirth == "" || signUpPw=="" || signUpCheckPw==""){
                Toast.makeText(
                    baseContext, "Please Fill the all blanks",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else{
                if(checkUserInfo(signUpNickname,signUpEmail, signUpBirth, signUpPw, signUpCheckPw)){ //중복된 사용자정보인지, 올바른 형식으로 입력했는지 확인
                    saveUserInfo(signUpNickname, signUpEmail, signUpBirth, signUpPw)
                    createAccount(signUpEmail, signUpPw)
                }
            }
        }
    }
    fun requirePermissions(permissions: Array<String>, requestCode: Int) {
        Log.d("permission","권한 요청");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionGranted(requestCode)
        } else {
            // isAllPermissionsGranted : 권한이 모두 승인 되었는지 여부 저장
            // all 메서드를 사용하면 배열 속에 들어 있는 모든 값을 체크할 수 있다.
            val isAllPermissionsGranted =
                permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
            if (isAllPermissionsGranted) {
                permissionGranted(requestCode)
            } else {
                // 사용자에 권한 승인 요청
                ActivityCompat.requestPermissions(this, permissions, requestCode)
                permissionDenied(requestCode)
            }
        }
    }

    private fun permissionGranted(requestCode: Int) {
        openGallery()
    }

    private fun permissionDenied(requestCode: Int) {
        when (requestCode) {
            PERMISSION_Album -> Toast.makeText(
                this,
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

        if (resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                profileImgView.setImageURI(uri)
            }
        }
    }


    //정규식 체크
    private fun checkUserInfo(nickname : String, email : String, birth : String, password : String, chekPassword : String) : Boolean{
        val birthPattern = "^((19|20)\\d\\d)?([-/.])?(0[1-9]|1[012])([-/.])?(0[1-9]|[12][0-9]|3[01])$" //YYYYMMDD
        val passwordPattern = """^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^+\-=])(?=\S+$).*$""" //숫자, 영어, 특수문자의 조합(하나 이상 포함), 공백 포함 불가
        //중복된 사용자인가?
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //이메일 정규식 확인
            Toast.makeText(
                baseContext,"올바른 이메일 형식이 아닙니다.",
                Toast.LENGTH_SHORT
            ).show();
            return false;
        }
        if(!Pattern.matches(birthPattern, birth)) { //생년월일 정규식 확인
            Toast.makeText(
                baseContext,"올바른 생년월일 입력 형식이 아닙니다.",
                Toast.LENGTH_SHORT
            ).show();
            return false;
        }
        if(!Pattern.matches(passwordPattern, password)) {
            Toast.makeText(
                baseContext,"최소 하나의 문자와 숫자를 포함한 8자 이상의 비밀번호가 필요합니다.",
                Toast.LENGTH_SHORT
            ).show();
            return false;
        }
        else if(password != chekPassword) {
            Toast.makeText(
                baseContext, "비밀번호가 다릅니다.",
                Toast.LENGTH_SHORT
            ).show()
            return false;
        }
        return true
    }

    private fun saveUserInfo(nickname : String, email : String, birth:String, password : String) {
        val userData = hashMapOf(
            "nickname" to nickname,
            "email" to email,
            "birth" to birth,
            "password" to password
        )


        usersCollectionReference.document(email).set(userData)
            .addOnSuccessListener {
            Log.d("message", "success")
        }.addOnFailureListener {}

    }

    //계정 생성
    private fun createAccount(email: String, password : String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "Success to Sign Up. Welcome!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this, "Failed to Sign Up. Try Again!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

}


