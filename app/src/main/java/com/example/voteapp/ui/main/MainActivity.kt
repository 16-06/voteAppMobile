package com.example.voteapp.ui.main

import android.content.Intent
import android.os.Bundle
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


class MainActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        if(token == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchVotes()
    }

    private fun fetchVotes() {
        RetrofitInstance.getApi(this).getAllVotes().enqueue(object: Callback<List<Vote>> {
            override fun onResponse(call: Call<List<Vote>>, response: Response<List<Vote>>) {
                if(response.isSuccessful){
                    val votes = response.body() ?: emptyList()
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
