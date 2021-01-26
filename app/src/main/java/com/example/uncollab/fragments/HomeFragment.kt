package com.example.uncollab.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uncollab.R
import com.example.uncollab.Util.DATA_POSTS
import com.example.uncollab.Util.Post
import com.example.uncollab.adapters.PostListAdapter
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : PostFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        postAdapter = PostListAdapter(userId!!, arrayListOf())
        postList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context, DividerItemDecoration.VERTICAL
                )
            )
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            updateList()
        }

    }

    override fun updateList() {
        postList?.visibility = View.GONE
        currentUser?.let {
            val posts = arrayListOf<Post>()

            firebaseDB.collection(DATA_POSTS).get()
                .addOnSuccessListener { list ->
                    for (document in list.documents) {
                        val post = document.toObject(Post::class.java)
                        post?.let {
                            posts.add(it)
                        }
                    }
                    updateAdapter(posts)
                    postList?.visibility = View.VISIBLE
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    postList?.visibility = View.VISIBLE
                }
        }
    }

    private fun updateAdapter(posts: List<Post>) {
        val sortedPosts = posts.sortedWith(compareByDescending { it.timeStamp })
        postAdapter?.updatePosts(removeDuplicates(sortedPosts))
    }

    private fun removeDuplicates(originalPosts: List<Post>) = originalPosts.distinctBy { it.postId }
}