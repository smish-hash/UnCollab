package com.example.uncollab.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.uncollab.HomeActivity
import com.example.uncollab.R
import com.example.uncollab.Util.DATA_USERS
import com.example.uncollab.Util.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        buttonRegister.setOnClickListener {
            onSignUp()
        }

        signInTV.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        setTextChangeListener(usernameET, usernameTIL)
        setTextChangeListener(emailET, emailTIL)
        setTextChangeListener(passwordET, passwordTIL)

        //        We dont want the user to press SignUp many times...this prevents it
        signUpProgressLayout.setOnTouchListener { v, event -> true }

    }

    private fun onSignUp() {
        var proceed = true

        if (usernameET.text.isNullOrEmpty()) {
            usernameTIL.error = "Username required"
            usernameTIL.isErrorEnabled = true
            proceed = false
        }
        if (emailET.text.isNullOrEmpty()) {
            emailTIL.error = "Email is required"
            emailTIL.isErrorEnabled = true
            proceed = false
        }

        if (passwordET.text.isNullOrEmpty()) {
            passwordTIL.error = "Password is required"
            passwordTIL.isErrorEnabled = true
            proceed = false
        }

        if (proceed) {
            signUpProgressLayout.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    firebaseAuth.createUserWithEmailAndPassword(
                        emailET.text.toString(),
                        passwordET.text.toString()
                    ).await()
                    withContext(Dispatchers.Main) {
                        val email = emailET.text.toString()
                        val name = usernameET.text.toString()

                        val user = User(email, name, "")

                        firebaseDB.collection((DATA_USERS)).document(firebaseAuth.uid!!).set(user)

                        Toast.makeText(this@SignUpActivity, "Account Created", Toast.LENGTH_SHORT)
                            .show()
                        Intent(this@SignUpActivity, HomeActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }

                        signUpProgressLayout.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                til.isErrorEnabled = false
//                removes the error the moment something is typed again
            }
        })
    }
}