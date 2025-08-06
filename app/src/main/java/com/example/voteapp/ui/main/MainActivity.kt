package com.example.voteapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voteapp.R
import com.example.voteapp.data.model.Vote
import com.example.voteapp.data.network.RetrofitInstance
import com.example.voteapp.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.edit
import com.example.voteapp.ui.vote.CreateVoteActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
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

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fabCreateVote = findViewById(R.id.fab_create_vote)
        bottomNav = findViewById(R.id.bottom_navigation)

        fabCreateVote.setOnClickListener {
            startActivity(Intent(this, CreateVoteActivity::class.java))
        }

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    true
                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, CreateVoteActivity::class.java))
                    true
                }
                else -> false
            }
        }

        fetchVotes()
    }

    private fun fetchVotes() {
        RetrofitInstance.getApi(this).getAllVotes().enqueue(object: Callback<List<Vote>> {
            override fun onResponse(call: Call<List<Vote>>, response: Response<List<Vote>>) {

                if(response.code() == 401){
                    getSharedPreferences("app_prefs", MODE_PRIVATE).edit { remove("jwt_token") }
                    startActivity(
                        Intent(this@MainActivity, LoginActivity::class.java)
                    )
                    finish()
                    return
                }

                if(response.isSuccessful){
                    val votes = response.body() ?: emptyList()
                    Log.d("VoteApp", "Votes: ${votes.map { it.id }}")
                    recyclerView.adapter = VoteAdapter(votes)
                }
                else{
                    Toast.makeText(this@MainActivity, "Api error", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Vote>>, t: Throwable){
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
