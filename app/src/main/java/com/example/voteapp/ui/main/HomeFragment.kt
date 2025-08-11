package com.example.voteapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voteapp.R
import com.example.voteapp.data.model.Vote
import com.example.voteapp.data.network.RetrofitInstance
import com.example.voteapp.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment: Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = VoteAdapter(emptyList())
        fetchVotes()

        return view
    }

    private fun fetchVotes() {
        RetrofitInstance.getApi(requireContext()).getAllVotes().enqueue(object: Callback<List<Vote>> {
            override fun onResponse(call: Call<List<Vote>>, response: Response<List<Vote>>) {

                if(response.isSuccessful){
                    val votes = response.body() ?: emptyList()
                    Log.d("VoteApp", "Votes: ${votes.map { it.id }}")
                    recyclerView.adapter = VoteAdapter(votes)
                }
                else{
                    Toast.makeText(requireContext(), "Api error", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Vote>>, t: Throwable){
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}