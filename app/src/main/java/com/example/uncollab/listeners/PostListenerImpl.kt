package com.example.uncollab.listeners

import android.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.uncollab.Util.DATA_POSTS
import com.example.uncollab.Util.DATA_POST_USER_IDS
import com.example.uncollab.Util.Post
import com.example.uncollab.Util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostListenerImpl(
    val postList: RecyclerView,
    var user: User?,
    val callback: HomeCallback?
) : PostListener {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onPostClick(post: Post?) {
        post?.let {
            AlertDialog.Builder(postList.context)
                .setTitle("Contact ${post.username}?")
                .setMessage("${post.email}")
                .setNegativeButton("Okay") { dialog, which ->
                }
                .show()
        }
    }

    override fun onSave(post: Post?) {
        post?.let {
            postList.isClickable = false
            val bookmarks = post.userIds
            if (bookmarks?.contains(userId) == true) {
                bookmarks?.remove(userId)
            } else {
                bookmarks?.add(userId!!)
            }
            firebaseDB.collection(DATA_POSTS).document(post.postId!!)
                .update(DATA_POST_USER_IDS, bookmarks)
                .addOnSuccessListener {
                    postList.isClickable = true
                    callback?.onRefresh()
                }
                .addOnFailureListener {
                    postList.isClickable = true
                }
        }

    }

}