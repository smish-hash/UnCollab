package com.example.uncollab.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.uncollab.Util.User
import com.example.uncollab.adapters.PostListAdapter
import com.example.uncollab.listeners.HomeCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.RuntimeException

abstract class PostFragment : Fragment() {
    protected var postAdapter: PostListAdapter? = null
    protected var currentUser: User? = null
    protected val firebaseDB = FirebaseFirestore.getInstance()
    protected val userId = FirebaseAuth.getInstance().currentUser?.uid

    protected var callback: HomeCallback? = null

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is HomeCallback){
//            callback = context
//        } else {
//            throw RuntimeException(context.toString() + " callback must be implemented")
//        }
//    }

    fun setUser(user: User?){
        this.currentUser = user
    }

    abstract fun updateList()

    override fun onResume() {
        super.onResume()
//        updateList()
    }
}