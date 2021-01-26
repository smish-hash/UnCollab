package com.example.uncollab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.uncollab.Util.DATA_USERS
import com.example.uncollab.Util.User
import com.example.uncollab.Util.loadUrl
import com.example.uncollab.activites.LoginActivity
import com.example.uncollab.activites.PostActivity
import com.example.uncollab.activites.ProfileActivity
import com.example.uncollab.fragments.HomeFragment
import com.example.uncollab.fragments.MyActivityFragment
import com.example.uncollab.fragments.PostFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var sectionsPagerAdapter: SectionsPagerAdapter? = null

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val fireBaseDB = FirebaseFirestore.getInstance()
    private val homeFragment = HomeFragment()
    private val myActivityFragment = MyActivityFragment()
    private var userID = FirebaseAuth.getInstance().currentUser?.uid

    private var user: User? = null

    private var currentFragment: PostFragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = sectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        tvTitleBar.text = "Home"
                        currentFragment = homeFragment
                    }
                    1 -> {
                        tvTitleBar.text = "My Activity"
                        currentFragment = myActivityFragment
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        logo.setOnClickListener {
            Intent(this, ProfileActivity::class.java).also {
                startActivity(it)
            }
        }

        fab.setOnClickListener {
            startActivity(PostActivity.newIntent(this, userID, user?.username, user?.email))
        }

        homeProgressLayout.setOnTouchListener { v, event -> true }


    }

    override fun onResume() {
//        a check in case user id is cull
        super.onResume()
        userID = FirebaseAuth.getInstance().currentUser?.uid
        if (userID == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
//        Load image on top corner
            populate()
        }
    }

    private fun populate() {
        homeProgressLayout.visibility = View.VISIBLE
        fireBaseDB.collection(DATA_USERS).document(userID!!).get()
            .addOnSuccessListener { documentSnapshot ->
                homeProgressLayout.visibility = View.GONE
                user = documentSnapshot.toObject(User::class.java)
                user?.imageUrl?.let {
                    logo.loadUrl(it, R.drawable.avatar_1)
                }
                updateFragmentUser()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
    }

    private fun updateFragmentUser() {
        homeFragment.setUser(user)
        myActivityFragment.setUser(user)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount() = 2

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> homeFragment
                1 -> myActivityFragment
                else -> homeFragment
            }
        }
    }
}