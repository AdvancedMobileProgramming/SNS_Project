package com.example.sns_project

import android.Manifest
import android.R
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.FragmentTransaction
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sns_project.databinding.FragmentPostingBinding
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
import kotlinx.android.synthetic.main.fragment_posting.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*


class PostingFragment: Fragment() { //????????? ????????? ??? R.layout.fragment_posting

    var mainActivity : MainActivity? = null

    private var imageURL: String? = null
    private val auth : FirebaseAuth = Firebase.auth //???????????? ????????? ??????
    private val db : FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference : CollectionReference = db.collection("post")
    private val storage : FirebaseStorage = Firebase.storage
    private val storageRef : StorageReference = storage.getReference()
    private lateinit var getResultImage: ActivityResultLauncher<Intent>
    private lateinit var bitmap : Bitmap
    private var imgDataUri : Uri ?= null
    val currentUserEmail = auth.currentUser?.email.toString()
    var currentUserNickname : String = ""



    // ????????? ?????? ????????? ???? ????????? null??? ?????? ???????????????. ( onDestroy ??? ??? ???????????? ????????? ???????????? )
    private var mBinding: FragmentPostingBinding? = null

    // ?????? null ????????? ??? ?????? ?????? ???????????? ?????? ????????? ?????? ??? ??????
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
        mainActivity = context as MainActivity

        mBinding = FragmentPostingBinding.inflate(inflater, container, false)

        binding.postingButton.setOnClickListener {
            val editTextTextMultiLine = binding.editTextTextMultiLine.text.toString()
            val imageButton = binding.imageButton

            if(editTextTextMultiLine == "") { //??????????????? ?????? ?????????
                Toast.makeText(
                    context, "Please Fill the all blanks",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if(imageButton == null) { //?????? ????????????
                Toast.makeText(
                    context, "???????????? ???????????????",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //HomeFragment ??????????????? ????????? ?????? ???????????? ?????? ??????,, ?????? ???
            this.uploadPost(
                binding.editTextTextMultiLine.text.toString()
            )
        }

        binding.imageButton.setOnClickListener { //?????????????????? ???????????? ?????? ?????? ????????????
            loadImage()
        }

        getResultImage = registerForActivityResult( //??????????????? ???????????? ???????????? ??????
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                imgDataUri = result.data?.data!!

                try {
                    bitmap =
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, imgDataUri)
                    binding.imageButton.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
                }
            }
        }

//        var transaction : FragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        Fragment2 fragment2 = new Fragment2();
//        transaction.replace(R.id.frameLayout, fragment2);
//        transaction.commit();


        return binding.root
    }

    fun loadImage() {
        var intent_image = Intent()
        intent_image.type = "image/*"
        intent_image.action = Intent.ACTION_GET_CONTENT
        getResultImage.launch(intent_image)
    }

    fun uploadPost(content: String) {
        if(binding.imageButton.drawable != null && this.imageURL == null) { //????????????
            Toast.makeText(
                context,
                "????????? ????????????..",
                Toast.LENGTH_SHORT
            ).show()
//            mainActivity.fragmentChange(2);
        }

        var timestamp = Timestamp.now()
        val time = timestamp.toDate()

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.data["email"].toString() == currentUserEmail){
                        //?????? uri??? ????????? ?????? ??????
                        val data = hashMapOf(
                            //"title" to title,
                            "content" to content,
                            "user" to auth.currentUser?.email.toString(), // ?????? ????????? ??? ?????? ????????? ?????????(?)
                            "nickname" to document.data["nickname"].toString(),
                            "created_at" to timestamp,
                            "image_uri" to imgDataUri
                        )

                        //????????? ????????? ??????(uri) storage??? ??????.
                        var storageRef = storage.reference
                        var postingImg = storageRef.child("image/posting/${currentUserEmail}${timestamp}.jpg")
                        var savePostingImg = imgDataUri?.let { postingImg.putFile(it) }

                        db.collection("post").document("${document.data["nickname"].toString()}${timestamp}")
                            .set(data)
                            .addOnCompleteListener {
                                Toast.makeText(
                                    context,
                                    "????????? ??????!",
                                    Toast.LENGTH_LONG
                                )
                                    .show()

                                binding.editTextTextMultiLine.setText("")
                                binding.imageButton.setImageBitmap(null)
                                //finish() // ???????????? ??????????????? ??? ????????? ???????????? ?????? ???????????? ?????????.
                            }
                            .addOnFailureListener {
                                Toast.makeText (
                                    context,
                                    "????????? ???????????? ?????? ???????????????.\n${it.message}",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                    }
                }
            }



    }


}