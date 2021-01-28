package com.example.uncollab.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uncollab.R
import com.example.uncollab.Util.DATA_POSTS
import com.example.uncollab.Util.DATA_POST_USER_IDS
import com.example.uncollab.Util.Post
import com.example.uncollab.adapters.PostListAdapter
import com.example.uncollab.listeners.PostListenerImpl
import kotlinx.android.synthetic.main.fragment_my_activity.*

class MyActivityFragment : PostFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener = PostListenerImpl(postList, currentUser, callback)

        postAdapter = PostListAdapter(userId!!, arrayListOf())
        postAdapter?.setListener(listener)
        postList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            updateList()
        }
    }

    override fun updateList() {
        postList?.visibility = View.GONE
        val posts = arrayListOf<Post>()

        firebaseDB.collection(DATA_POSTS).whereArrayContains(DATA_POST_USER_IDS, userId!!).get()
            .addOnSuccessListener { list ->
                for (document in list.documents) {
                    val post = document.toObject(Post::class.java)
                    post?.let {
                        posts.add(post)
                    }
                }

                val sortedList = posts.sortedWith(compareByDescending { it.timeStamp })
                postAdapter?.updatePosts(sortedList)
                postList?.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                postList?.visibility = View.VISIBLE
            }
    }
}
