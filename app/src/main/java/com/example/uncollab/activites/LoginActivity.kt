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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (firebaseAuth.currentUser != null){
            Intent(this, HomeActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        setTextChangeListener(emailET, emailTIL)
        setTextChangeListener(passwordET, passwordTIL)

        buttonLogin.setOnClickListener {
            onLogin()
        }

        signupTV.setOnClickListener {
            onSignUp()
        }

//        We dont want the user to press login many times...this prevents it
        loginProgressLayout.setOnTouchListener { v, event -> true }

    }

    private fun onSignUp() {
        Intent(this, SignUpActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun onLogin() {
        var proceed = true
        if (emailET.text.isNullOrEmpty()){
            emailTIL.error = "Email required"
            emailTIL.isErrorEnabled = true
            proceed = false
        }

        if (passwordET.text.isNullOrEmpty()){
            passwordTIL.error = "Password required"
            passwordTIL.isErrorEnabled = true
            proceed = false
        }

        if (proceed) {
            loginProgressLayout.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    firebaseAuth.signInWithEmailAndPassword(
                        emailET.text.toString(),
                        passwordET.text.toString()
                    ).await()
                    withContext(Dispatchers.Main) {
                        Intent(this@LoginActivity, HomeActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                        loginProgressLayout.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        loginProgressLayout.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
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