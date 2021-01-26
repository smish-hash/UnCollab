package com.example.uncollab.activites

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.uncollab.R
import com.example.uncollab.Util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB = FirebaseFirestore.getInstance()

    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val userID = firebaseAuth.currentUser?.uid
    private var imageUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (userID == null){
            finish()
        }

        profileProgressLayout.setOnTouchListener { view, motionEvent -> true }


        photoIV.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }

        populateInfo()

        applyButton.setOnClickListener {
            onApply()
        }

        signoutButton.setOnClickListener {
            onSignOut()
        }
    }

    private fun populateInfo() {
//        Might need to use coroutines
        profileProgressLayout.visibility = View.VISIBLE
        firebaseDB.collection(DATA_USERS).document(userID!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                usernameET.setText(user?.username, TextView.BufferType.EDITABLE)
                emailET.setText(user?.email, TextView.BufferType.EDITABLE)

                imageUrl = user?.imageUrl
                imageUrl?.let {
                    if (user != null) {
                        user.imageUrl?.let { it1 -> photoIV.loadUrl(it1, R.drawable.avatar_1) }
                    }
                }
//                See Util.kt...how image is being loaded
                profileProgressLayout.visibility = View.GONE
            }.addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
    }

    private fun onApply() {
//        When user presses apply changes button
        profileProgressLayout.visibility = View.VISIBLE
        val username = usernameET.text.toString()
        val email = emailET.text.toString()
        val map = HashMap<String, Any>()
        map[DATA_USER_USERNAME] = username
        map[DATA_USER_EMAIL] = email

//        Update the data base with new changes
        firebaseDB.collection(DATA_USERS).document(userID!!).update(map)
            .addOnSuccessListener {
                Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
//                Let the user try again
                it.printStackTrace()
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                profileProgressLayout.visibility = View.GONE
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK) {
            storeImage(data?.data)
        }
    }

    private fun storeImage(imageUri: Uri?) {
        imageUri?.let {
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()
            profileProgressLayout.visibility = View.VISIBLE
            val filePath = firebaseStorage.child(DATA_IMAGES)
                .child(userID!!)
            filePath.putFile(imageUri)
                .addOnSuccessListener {
                    filePath.downloadUrl.addOnSuccessListener {uri ->
                        val url = uri.toString()
                        firebaseDB.collection(DATA_USERS).document(userID!!).update(
                            DATA_USER_IMAGE_URL, url)
                            .addOnSuccessListener{
                                imageUrl = url
                                photoIV.loadUrl(imageUrl!!, R.drawable.avatar_1)
                            }
                        profileProgressLayout.visibility  = View.GONE
                    }.addOnFailureListener {
                        Toast.makeText(this, "Image Upload failed", Toast.LENGTH_SHORT).show()
                        profileProgressLayout.visibility = View.GONE
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Image Upload failed", Toast.LENGTH_SHORT).show()
                    profileProgressLayout.visibility = View.GONE
                }
        }
    }

    private fun onSignOut() {
        firebaseAuth.signOut()
        finish()
    }

}