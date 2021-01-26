package com.example.uncollab.listeners

import com.example.uncollab.Util.Post

interface PostListener {
    fun onPostClick(post: Post?)
    fun onSave(post: Post?)
}