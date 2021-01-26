package com.example.uncollab.Util

data class User (
    val email: String? = "",
    val username: String? = "",
    val imageUrl: String? = ""
)

data class Post(
    val postId: String? = "",
    val userIds: ArrayList<String>? = arrayListOf(),
    val email: String? = "",
    val username: String? = "",
    val text: String? = "",
    val imageUrl: String? = "",
    val timeStamp: Long? = 0,
)