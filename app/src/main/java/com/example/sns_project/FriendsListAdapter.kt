package com.example.sns_project

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sns_project.databinding.FriendsItemBinding
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.friends_item.view.*

class FriendsListAdapter(private var context: Context?) :
    RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    var datafriends = mutableListOf<DataFriends>()

    //viewHolder를 새로 만들어야 할 때 호출
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        val fbinding = FriendsItemBinding.inflate(view, parent, false)

        return ViewHolder(fbinding)

    }

    //데이터 목록 표시
    inner class ViewHolder(private val binding : FriendsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val userEmail: TextView = itemView.user
        private val userNickname: TextView = itemView.nickname
        private val profile: ImageView = itemView.profile


        fun bind(item: DataFriends) {
            displayImageRef(item.profile, profile)
            userEmail.text = item.user
            userNickname.text = item.nickname


            itemView.setOnClickListener {
                lateinit var navController: NavController
                navController = Navigation.findNavController(binding.root)
                val bundle = bundleOf("friend" to "${item.user}")
                navController.navigate(
                    com.example.sns_project.R.id.action_friendsFragment_to_friendProfileFragment,
                    bundle
                )

//                Toast.makeText(
//                    it.context, "Nickname : " + item.nickname, Toast.LENGTH_SHORT
//                ).show()
            }
        }
    }


    private fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
        imageRef!!.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(view.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(view)
        }
//            imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener{
//                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
//                view.setImageBitmap(bmp)
//            }?.addOnFailureListener {
//            }
    }

    //ViewHolder를 데이터와 연결할 때 호출
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datafriends[position])
    }

    //데이터 세트 크기를 가져올 때 호출
    override fun getItemCount(): Int {
        return datafriends.size
    }
}
