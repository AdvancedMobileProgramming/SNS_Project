package com.example.sns_project

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_myprofile.view.*

class MyProfileFragment : Fragment(R.layout.fragment_myprofile) { //내 프로필 조회
    private val auth: FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db: FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference: CollectionReference = db.collection("users")
    private val storage: FirebaseStorage = Firebase.storage
    private val storageRef :StorageReference = storage.getReference()

    val currentUserEmail = auth.currentUser?.email.toString()
//    private lateinit var imgDataUri : Uri

    fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener {
// Failed to download the image
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val profileView = view.profileView
        val nicknameTextView = view.nicknameTextView
        val birthTextView = view.birthTextView
        val descriptionTextView = view.descriptionTextView

        // 사용자 개인의 피드.

        val imageRef =storageRef.child("image/profile/${currentUserEmail}.jpg")
        displayImageRef(imageRef, profileView) //사용자 프로필 이미지 보이기.


    usersCollectionReference.document(currentUserEmail).get()
    .addOnSuccessListener {
        nicknameTextView.text = it["nickname"].toString()
        birthTextView.text = it["birth"].toString()
        if(it["description"].toString() === null) descriptionTextView.text = "소개글이 없습니다."
        else descriptionTextView.text = it["description"].toString()
    }

    }
}