package com.example.voteapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voteapp.R
import com.example.voteapp.data.network.RetrofitInstance
import com.example.voteapp.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.edit
import com.example.voteapp.data.model.AuthenticatedUserDto
import com.example.voteapp.ui.profile.UserProfileFragment
import com.example.voteapp.ui.vote.create.CreateVoteFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    private lateinit var fabCreateVote: FloatingActionButton
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        if(token == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        fabCreateVote = findViewById(R.id.fab_create_vote)
        bottomNav = findViewById(R.id.bottom_navigation)

        fabCreateVote.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateVoteFragment())
                .addToBackStack(null)
                .commit()
        }

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()

        }

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                    true
                }
                R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, UserProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }


        userAuthenticationCheck()
    }

    private fun userAuthenticationCheck() {
        val api = RetrofitInstance.getApi(this)

        api.getAuthenticatedUser().enqueue(object : Callback<AuthenticatedUserDto>{
            override fun onResponse(call: Call<AuthenticatedUserDto>, response: Response<AuthenticatedUserDto>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if( user != null) {
                        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                        prefs.edit {
                            putLong("userId", user.id)
                                .putString("username", user.username)
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AuthenticatedUserDto>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error status: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
