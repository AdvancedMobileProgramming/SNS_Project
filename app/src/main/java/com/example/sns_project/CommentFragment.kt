package com.example.sns_project

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sns_project.databinding.CommentItemBinding
import com.example.sns_project.databinding.FragmentCommentBinding
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
import kotlinx.android.synthetic.main.fragment_comment.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class CommentFragment : Fragment(R.layout.fragment_comment) {
    private val auth: FirebaseAuth = Firebase.auth //사용자의 계정을 관리
    private val db: FirebaseFirestore = Firebase.firestore
    private val usersCollectionReference: CollectionReference = db.collection("users")
    private val postsCollectionReference: CollectionReference = db.collection("post")
    private val storage: FirebaseStorage = Firebase.storage
    private val storageRef: StorageReference = storage.getReference()

    val currentUserEmail = auth.currentUser?.email.toString()

    // 바인딩 객체 타입에 ?를 붙여서 null을 허용 해줘야한다. ( onDestroy 될 때 완벽하게 제거를 하기위해 )
    private var mBinding: FragmentCommentBinding? = null
    private var iBinding: CommentItemBinding? = null


    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    private val ibinding get() = iBinding!!

    lateinit var commentRecyclerAdapter: CommentRecyclerAdapter
    val comments = ArrayList<CommentDTO>()

    private val viewModel: CommentViewModel by viewModels()



    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentCommentBinding.inflate(inflater, container, false)

//        val adapter = CommentRecyclerAdapter(this.requireContext(), comments)
//        binding.root.comment_recycler.adapter = adapter // RecyclerView와 CustomAdapter 연결
//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.setHasFixedSize(true)

        initRecycler()

        commentRecyclerAdapter = CommentRecyclerAdapter(viewModel, comments)
        binding.root.comment_recycler.adapter = commentRecyclerAdapter
        binding.root.comment_recycler.layoutManager = LinearLayoutManager(this.context)

        binding.root.comment_recycler.addItemDecoration(DividerItemDecoration(this.context, 1))

        val profileView = binding.imageView3
        val imageRef = storageRef.child("image/profile/${currentUserEmail}.jpg")
        displayImageRef(imageRef, profileView) //사용자 프로필 이미지 보이기.

        binding.commentButton.setOnClickListener {
            val comment = binding.commentEditText.text.toString()

            val data = hashMapOf(
                "content" to comment ,
                "user" to currentUserEmail,
                "date" to Timestamp.now().toDate()
            )

            hideKeyboard()

            Log.d("haha", "${arguments?.getString("post")}")
            db.collection("post").document("${arguments?.getString("post")}")
                .collection("comments").add(data)
                .addOnCompleteListener {
                    Toast.makeText(
                        context,
                        "댓글 업로드 완료!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    viewModel.addItem(
                        CommentDTO(
                            profile = storageRef.child("image/profile/${currentUserEmail}.jpg"),
                            content = "${data["content"]}",
                            user = "${data["user"]}",
                            time = "${data["date"]}"
                        )
                    )
//                    lateinit var navController: NavController
//                    navController = Navigation.findNavController(binding.root)
//                    navController.navigate(com.example.sns_project.R.id.action_postingFragment_to_homeFragment)
                    //finish() // 업로드가 성공한다면 이 화면을 종료하고 메인 페이지로 돌아감.
//                    viewModel.addItem(CommentDTO(content = data.get("content").toString(), user = data.get("user").toString(), profile = imageRef))
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "댓글 업로드에 실패 하였습니다.\n${it.message}",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
        }

        val dataObserver: Observer<ArrayList<CommentDTO>> =
            Observer { livedata ->
                commentRecyclerAdapter = CommentRecyclerAdapter(viewModel, comments)
                binding.root.comment_recycler.adapter = commentRecyclerAdapter
                commentRecyclerAdapter.comments = viewModel.comments
            }

        viewModel.commentsLiveData.observe(viewLifecycleOwner, dataObserver)

        commentRecyclerAdapter = CommentRecyclerAdapter(viewModel, comments)
        binding.root.comment_recycler.adapter = commentRecyclerAdapter

        return binding.root
    }

    private fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler() {
        commentRecyclerAdapter = CommentRecyclerAdapter(viewModel, comments)
        binding.root.comment_recycler.adapter = commentRecyclerAdapter

        binding.root.comment_recycler.addItemDecoration(DividerItemDecoration(this.context, 1))

        CoroutineScope(Dispatchers.Default).launch {
            db.collection("post").document("${arguments?.getString("post")}")
                .collection("comments")
                .get()
                .addOnSuccessListener { result ->
                    comments.clear()
                    for (document in result) {
                        val nickname = getNickname(document.data["user"].toString())
                        viewModel.addItem(
                            CommentDTO(
                                profile = storageRef.child("image/profile/${document.data["user"].toString()}.jpg"),
                                content = "${document.data["content"].toString()}",
                                user = "${document.data["user"].toString()}",
                                time = "${document.data["date"]}"
                            )
                        )
                    }


                }
                .addOnFailureListener { exception ->
                    Log.d("error", "error")
                }
        }


    }

    fun getNickname(user : String) {
        db.collection("users").document(user)
            .get()
            .addOnSuccessListener { result ->
                 //..미해결
                }
            }
    }

        fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
            imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                view.setImageBitmap(bmp)
            }?.addOnFailureListener {
// Failed to download the image
            }
        }




