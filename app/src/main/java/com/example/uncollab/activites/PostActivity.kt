package com.example.uncollab.activites

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.uncollab.R
import com.example.uncollab.Util.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_post.*

class PostActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private var imageUrl: String? = null
    private var userId: String? = null
    private var username: String? = null
    private var email: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        if (intent.hasExtra(PARAM_USER_ID) && intent.hasExtra(PARAM_USER_NAME) && intent.hasExtra(
                PARAM_USER_EMAIL
            )
        ) {
            userId = intent.getStringExtra(PARAM_USER_ID)
            username = intent.getStringExtra(PARAM_USER_NAME)
            email = intent.getStringExtra(PARAM_USER_EMAIL)

        } else {
            Toast.makeText(this, "Error CreatingTweet", Toast.LENGTH_SHORT).show()
            finish()
        }

        fabPhoto.setOnClickListener {
            addImage()
        }

        setTextChangeListener(postText, postTextTIL)

        fabSend.setOnClickListener {
            if (postText.text.toString().isNullOrEmpty()){
                postTextTIL.error = "Cannot be blank"
                postTextTIL.isErrorEnabled = true
            }else{
                onPost()
            }
        }

        btnBackPost.setOnClickListener {
            onBackPressed()
        }

        postProgressLayout.setOnTouchListener { v, event -> true }


    }

    private fun onPost() {
        postProgressLayout.visibility = View.VISIBLE
        val text = postText.text.toString()
        val postId = firebaseDB.collection(DATA_POSTS).document()
        val post = Post(
            postId.id,
            arrayListOf(userId!!),
            email,
            username,
            text,
            imageUrl,
            System.currentTimeMillis()
        )

//        Adding post to database
        postId.set(post)
            .addOnCompleteListener { finish() }
            .addOnFailureListener { e ->
                e.printStackTrace()
                postProgressLayout.visibility = View.GONE
                Toast.makeText(this, "Post Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            storeImage(data?.data)
        }
    }

    fun storeImage(imageUri: Uri?) {
        imageUri?.let {
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()
            postProgressLayout.visibility = View.VISIBLE

            val filePath = firebaseStorage.child(DATA_IMAGES).child(userId!!)
            filePath.putFile(imageUri)
                .addOnSuccessListener {
                    filePath.downloadUrl
                        .addOnSuccessListener { uri ->
                            imageUrl = uri.toString()
                            tweetImage.loadUrl(imageUrl!!, R.drawable.avatar_1)
                            postProgressLayout.visibility = View.GONE
                        }
                        .addOnFailureListener {
                            onUploadFailure()
                        }
                }
                .addOnFailureListener {
                    onUploadFailure()
                }
        }
    }

    fun onUploadFailure() {
        Toast.makeText(this, "Image upload failed. Please try agail later.", Toast.LENGTH_SHORT)
            .show()
        postProgressLayout.visibility = View.GONE
    }

    companion object {
        val PARAM_USER_ID = "UserId"
        val PARAM_USER_NAME = "Username"
        val PARAM_USER_EMAIL = "Email"

        fun newIntent(
            context: Context,
            userId: String?,
            userName: String?,
            email: String?
        ): Intent {
            val intent = Intent(context, PostActivity::class.java)
            intent.putExtra(PARAM_USER_ID, userId)
            intent.putExtra(PARAM_USER_NAME, userName)
            intent.putExtra(PARAM_USER_EMAIL, email)
            return intent
        }
    }

    //    To not keep showing the error message continuously
    fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                til.isErrorEnabled = false
//                removes the error the moment something is typed again
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
}