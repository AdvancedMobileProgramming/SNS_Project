package com.example.sns_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        auth = Firebase.auth

        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.editTextID).text.toString()
            val userPw = findViewById<EditText>(R.id.editTextPW).text.toString()

            if(userEmail == null) {
                Toast.makeText(
                    baseContext, "Put in your E-mail",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if(userPw == null){
                Toast.makeText(
                    baseContext, "Put in your Password",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if (userEmail != null && userPw != null) signIn(userEmail, userPw)

        }

        val signUpButton = findViewById<Button>(R.id.moveSignUpButton)
        signUpButton.setOnClickListener{
            moveSignUpPage()
        }
    }

    //로그인 함수.
    private fun signIn(id: String, pw: String) {
        if (id.isNotEmpty() && pw.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(id, pw)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "Success to Login. Welcome!",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveMainPage(auth?.currentUser)
                    } else {
                        Toast.makeText(
                            baseContext, "Failed to Login. Check your account.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    //회원가입 창으로 이동
    private fun moveSignUpPage(){
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    // 유저정보 넘겨주고 메인 액티비티 호출
    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}


