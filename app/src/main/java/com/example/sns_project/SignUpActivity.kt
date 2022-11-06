package com.example.sns_project

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern
import java.util.stream.Collector


data class userInfo (
    var nickname :String? = null,
    var email : String? = null,
    var birth : String? = null,
    var password : String? = null
)

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth//사용자의 계정을 관리
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth =  Firebase.auth
        db = FirebaseFirestore.getInstance()

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
        else if(password != chekPassword){
            Toast.makeText(
                baseContext, "비밀번호가 다릅니다.",
                Toast.LENGTH_SHORT
            ).show()
            return false;
        }
        
        return true
    }

    private fun saveUserInfo(nickname : String, email : String, birth:String, password : String){
        val userData = hashMapOf(
            "nickname" to nickname,
            "email" to email,
            "birth" to birth,
            "password" to password
        )

        val collectionRef : CollectionReference = db.collection("users")
        val task : Task<DocumentReference> = collectionRef.add(userData)
        task.addOnSuccessListener { documentReference ->
            Log.d("doc_id","DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { exception ->
            Log.d("doc_err","Error adding document: ${exception.toString()}")
        }

        var userInfo = userInfo()
        userInfo.nickname = nickname
        userInfo.email = email
        userInfo.birth = birth
        userInfo.password = password

//        db?.collection("users")
//            ?.add(data)
//            ?.addOnSuccessListener {
//                // 성공할 경우
//                Toast.makeText(this, "데이터가 추가되었습니다", Toast.LENGTH_SHORT).show()
//            }
//            ?.addOnFailureListener { exception ->
//                // 실패할 경우
//                Log.w("MainActivity", "Error getting documents: $exception")
//            }
    }
//        db?.collection("users")
//            ?.add(userInfo)
//
//        }

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


