package com.example.voteapp.ui.vote.create

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voteapp.R
import com.example.voteapp.data.model.VoteResponseDto
import com.example.voteapp.data.network.RetrofitInstance
import com.example.voteapp.ui.vote.option.AddOptionsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateVoteActivity : AppCompatActivity() {

    private lateinit var voteNameInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var createVoteButton: Button

    private val categories = listOf("lifestyle", "sport", "polityka", "zabawne", "motoryzacja", "informatyka")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_vote)

        voteNameInput =  findViewById<EditText>(R.id.voteNameInput)
        categorySpinner = findViewById(R.id.categorySpinner)
        createVoteButton = findViewById(R.id.createVoteButton)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        createVoteButton.setOnClickListener{
            createVote()
        }

    }

    private fun createVote() {

        val name = voteNameInput.text.toString()
        val category = categorySpinner.selectedItem.toString()

        if (name.isBlank()) {
            Toast.makeText(this, "Name needed", Toast.LENGTH_SHORT).show()
            return
        }

        val voteData = mapOf(
            "name" to name,
            "category" to category
        )
        val api = RetrofitInstance.getApi(this)

        api.createVote(voteData).enqueue(object : Callback<VoteResponseDto> {
            override fun onResponse(call: Call<VoteResponseDto>, response: Response<VoteResponseDto>){
                if(response.isSuccessful){
                    val vote = response.body()!!
                    Toast.makeText(this@CreateVoteActivity, "Vote created: ${vote.name}", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@CreateVoteActivity, AddOptionsActivity::class.java)
                    intent.putExtra("voteId", vote.id)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this@CreateVoteActivity, "Error creating vote: ${response.message()}", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onFailure(call: Call<VoteResponseDto>, t: Throwable) {
                Toast.makeText(this@CreateVoteActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

}