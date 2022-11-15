package com.example.sns_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment(R.layout.fragment_setting) {
    private val auth : FirebaseAuth = Firebase.auth //사용자의 계정을 관리

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val logOutBtn = view.LogOutButton
        val signOutBtn = view.signOutButton

        logOutBtn.setOnClickListener {
            auth.signOut()
            startActivity(
                Intent(context, SignInActivity::class.java)
            )
        }

        signOutBtn.setOnClickListener {
            auth.currentUser?.delete();
            AlertDialog.Builder(view.context)
                .setTitle("회원 탈퇴")
                .setMessage("탈퇴하시겠습니까?")
                .show()

            startActivity(
                Intent(context, SignInActivity::class.java)
            )

        }
    }

}