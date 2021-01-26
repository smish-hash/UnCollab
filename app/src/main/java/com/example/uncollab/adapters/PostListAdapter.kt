package com.example.uncollab.adapters

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.uncollab.R
import com.example.uncollab.Util.Post
import com.example.uncollab.Util.getDate
import com.example.uncollab.Util.loadUrl
import com.example.uncollab.listeners.PostListener

class PostListAdapter(val userId: String, val posts: ArrayList<Post>) :
    RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    private var listener: PostListener? = null

    fun setListener(listener: PostListener?) {
        this.listener = listener
    }

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layout = itemView.findViewById<ViewGroup>(R.id.postLayout)
        private val username = itemView.findViewById<TextView>(R.id.postUsername)
        private val text = itemView.findViewById<TextView>(R.id.postCaption)
        private val image = itemView.findViewById<ImageView>(R.id.postImage)
        private val date = itemView.findViewById<TextView>(R.id.postDate)
        private val bookmark = itemView.findViewById<ImageView>(R.id.postBookmark)
        private val bookmarkCount = itemView.findViewById<TextView>(R.id.postBookmarkCount)

        fun bind(userId: String, post: Post, listener: PostListener?) {
            username.text = post.username
            text.text = post.text
//            image.setImageResource(R.drawable.avatar_1)
//            post.imageUrl?.let { image.loadUrl(it) }

            if (post.imageUrl.isNullOrEmpty()) {
                image.visibility = View.GONE
            } else {
                image.visibility = View.VISIBLE
                image.loadUrl(post.imageUrl)
            }
            date.text = getDate(post.timeStamp)
//            To remove owner's count
            bookmarkCount.text = post.userIds?.size?.minus(1).toString()

            layout.setOnClickListener { listener?.onPostClick(post) }
            bookmark.setOnClickListener { listener?.onSave(post) }

            if (post.userIds?.get(0).equals(userId)) {
                bookmark.visibility = View.INVISIBLE
                bookmark.isClickable = false
            } else if (post.userIds?.contains(userId) == true) {
                bookmark.setImageDrawable(
                    ContextCompat.getDrawable(
                        bookmark.context,
                        R.drawable.ic_baseline_bookmark_24
                    )
                )
            } else {
                bookmark.setImageDrawable(
                    ContextCompat.getDrawable(
                        bookmark.context,
                        R.drawable.ic_baseline_bookmark_border_24
                    )
                )
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(userId, posts[position], listener)
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}