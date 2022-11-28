package com.example.sns_project

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern


data class userInfo (
    var nickname :String? = null,
    var email : String? = null,
    var birth : String? = null,
    var password : String? = null
)

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {
    private val auth : FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db : FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference : CollectionReference = db.collection("users")
    private val storage : FirebaseStorage = Firebase.storage
    private lateinit var imgDataUri : Uri
    private lateinit var bitmap : Bitmap
    private lateinit var getResultImage: ActivityResultLauncher<Intent>

    private lateinit var signUpProfile : Uri
    private lateinit var signUpNickname : String
    private lateinit var signUpEmail : String
    private lateinit var signUpBirth : String
    private lateinit var signUpPw : String
    private lateinit var signUpCheckPw : String

    private val PERMISSION_Album = 101
    private val REQUEST_STORAGE = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val profileImgView = findViewById<ImageView>(R.id.profileImgView);
        val selectProfileButton = findViewById<Button>(R.id.selectProfileButton);
        selectProfileButton.setOnClickListener { //이미지버튼에 겔러리에 가서 사진 받아오기
            loadImage()
        }

        getResultImage = registerForActivityResult( //갤러리에서 이미지를 가져오게 하기
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                imgDataUri = result.data?.data!!

                try {
                    bitmap =
                        MediaStore.Images.Media.getBitmap(this?.contentResolver, imgDataUri)
                    profileImgView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {

//            val signUpProfile = findViewById<ImageView>(R.id.profileImgView)
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
                if(checkEmailInfo(signUpEmail) && checkUserInfo(signUpNickname) && checkBirthInfo(signUpBirth) && checkPasswordInfo(signUpPw, signUpCheckPw)){ //중복된 사용자정보인지, 올바른 형식으로 입력했는지 확인
                    saveUserInfo(signUpNickname, signUpEmail, signUpBirth, signUpPw)
//                    saveUserImg();
                    createAccount(signUpEmail, signUpPw)
                }
            }
        }
    }

    fun loadImage() {
        var intent_image = Intent()
        intent_image.type = "image/*"
        intent_image.action = Intent.ACTION_GET_CONTENT
        getResultImage.launch(intent_image)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                profileImgView.setImageURI(uri)
                signUpProfile = uri
            }
        }
    }

    private fun checkEmailInfo(signUpEmail : String) : Boolean{
        val pattern = Patterns.EMAIL_ADDRESS
        var isCorrect : Boolean = true
        CoroutineScope(Dispatchers.Default).launch {
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if(document.data["email"].toString().equals(signUpEmail)) isCorrect = false
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("error", "Error getting documents.", exception)
                }

            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(signUpEmail).matches()) { //이메일 정규식 확인
                Toast.makeText(
                    baseContext,"올바른 이메일 형식이 아닙니다.",
                    Toast.LENGTH_SHORT
                ).show();
                isCorrect =  false;
            }
        }

        return isCorrect;
    }

    private fun checkUserInfo(signUpNickname : String) : Boolean{
        var isCorrect : Boolean = true
        CoroutineScope(Dispatchers.Default).launch {
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if(document.data["nickname"].toString().equals(signUpNickname)) isCorrect = false
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("error", "Error getting documents.", exception)
                }
        }
        return isCorrect;
    }

    private fun checkBirthInfo( signUpBirth : String) : Boolean{
        val birthPattern = "^((19|20)\\d\\d)?([-/.])?(0[1-9]|1[012])([-/.])?(0[1-9]|[12][0-9]|3[01])$" //YYYYMMDD
        //중복된 사용자인가?

        if(!Pattern.matches(birthPattern, signUpBirth)) { //생년월일 정규식 확인
            Toast.makeText(
                baseContext,"올바른 생년월일 입력 형식이 아닙니다.",
                Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        return true
    }

    //정규식 체크
    private fun checkPasswordInfo(password : String, chekPassword : String) : Boolean{
        val passwordPattern = """^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^+\-=])(?=\S+$).*$""" //숫자, 영어, 특수문자의 조합(하나 이상 포함), 공백 포함 불가

        if(!Pattern.matches(passwordPattern, password)) {
            Toast.makeText(
                baseContext,"최소 하나의 영문자와 숫자, 특수문자를 포함한 8자 이상의 비밀번호가 필요합니다.",
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
        //프로필 이미지 정보(uri) storage에 저장.
        var storageRef = storage.reference
        var postingImg = storageRef.child("image/profile/${email}.jpg")
        var savePostingImg = postingImg.putFile(imgDataUri)

        val userData = hashMapOf(
//            "profileImg" to postingImg.toString(),
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
                            this, "Already exist. Try Again!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

}


