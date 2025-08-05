package com.example.voteapp.ui.vote

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voteapp.R
import com.example.voteapp.data.api.VoteApi
import com.example.voteapp.data.model.Comment
import com.example.voteapp.data.model.VoteOption
import com.example.voteapp.data.model.WhoVotedYetRequestDto
import com.example.voteapp.data.model.WhoVotedYetResponseDto
import com.example.voteapp.data.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoteDetailsActivity : AppCompatActivity() {

    private lateinit var optionsContainer: LinearLayout
    private lateinit var commentsContainer: LinearLayout
    private lateinit var commentInput: EditText
    private lateinit var commentButton: Button
    private lateinit var api: VoteApi

    private var voteId: Int = -1
    private var hasVoted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_details)
        api = RetrofitInstance.getApi(this)

        voteId = intent.getIntExtra("voteId", -1)
        if (voteId == -1) {
            Toast.makeText(this, "ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        optionsContainer = findViewById(R.id.optionsContainer)
        commentsContainer = findViewById(R.id.commentsContainer)
        commentInput = findViewById(R.id.commentInput)
        commentButton = findViewById(R.id.commentButton)

        loadOptions()
        loadComments()

        commentButton.setOnClickListener {
            val commentText = commentInput.text.toString().trim()
            if (commentText.isNotEmpty()) {
                postComment(commentText)
            } else {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun loadOptions() {
        api.hasUserVoted(voteId.toLong()).enqueue(object : Callback<Map<String, Boolean>> {
            override fun onResponse(call: Call<Map<String, Boolean>>, response: Response<Map<String, Boolean>>) {
                if(response.isSuccessful && response.body()?.get("hasVoted") == true){
                    loadVoteOptions(disableButtons = true)

                }
                else{
                    loadVoteOptions(disableButtons = false)
                }
            }
            override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                Toast.makeText(this@VoteDetailsActivity, "Error checking vote status: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadVoteOptions(disableButtons: Boolean) {
        api.getVoteOptions(voteId).enqueue(object : Callback<List<VoteOption>> {
            override fun onResponse(call: Call<List<VoteOption>>, response: Response<List<VoteOption>>) {
                if (response.isSuccessful) {
                    optionsContainer.removeAllViews()
                    response.body()?.forEach { option ->
                        val optionView = layoutInflater.inflate(R.layout.item_vote_option, null)
                        val name = optionView.findViewById<TextView>(R.id.optionName)
                        val count = optionView.findViewById<TextView>(R.id.optionCount)
                        val voteButton = optionView.findViewById<Button>(R.id.voteButton)

                        name.text = option.name
                        count.text = option.count.toString()

                        if(disableButtons){
                            voteButton.isEnabled = false
                            voteButton.text = "Voted yet"
                        } else {
                            voteButton.setOnClickListener{
                                vote(option.id)
                            }
                        }

                        optionsContainer.addView(optionView)
                    }
                }
            }
            override fun onFailure(call: Call<List<VoteOption>>, t: Throwable) {
                Toast.makeText(this@VoteDetailsActivity, "Error loading options: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun vote(optionId: Long) {
        if (hasVoted) {
            Toast.makeText(this, "You have already voted", Toast.LENGTH_SHORT).show()
            return
        }

        api.voteOption(mapOf("id" to optionId)).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Toast.makeText(this@VoteDetailsActivity, "Vote recorded", Toast.LENGTH_SHORT).show()
                    markAsVoted()
                    loadOptions()
                } else {
                    Toast.makeText(this@VoteDetailsActivity, "Failed to vote", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@VoteDetailsActivity, "Error voting: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun markAsVoted() {

        val body = WhoVotedYetRequestDto(
            voteId = voteId.toLong(),
            alreadyVoted = true
        )

        api.markAsVoted(body).enqueue(object : Callback<WhoVotedYetResponseDto> {
            override fun onResponse(call: Call<WhoVotedYetResponseDto>, response: Response<WhoVotedYetResponseDto>) {
                if(response.isSuccessful){
                    hasVoted = true
                    Toast.makeText(this@VoteDetailsActivity, "Marked as voted", Toast.LENGTH_SHORT).show()
                    loadOptions()
                } else {
                    Toast.makeText(this@VoteDetailsActivity, "Failed to mark as voted", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<WhoVotedYetResponseDto>, t: Throwable) {
                Toast.makeText(this@VoteDetailsActivity, "Error marking as voted: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun postComment(commentText: String) {
        api.postComment(mapOf("commentBody" to commentText, "voteId" to voteId)).enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if(response.isSuccessful){
                    commentInput.text.clear()
                    loadComments()
                }
                else{
                    Toast.makeText(this@VoteDetailsActivity, "Failed to post comment", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
               Toast.makeText(this@VoteDetailsActivity, "Error posting comment: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadComments() {
        api.getComments(voteId).enqueue(object : Callback<List<Comment>> {
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if (response.isSuccessful) {
                    commentsContainer.removeAllViews()
                    response.body()?.forEach { comment ->
                        val textView = TextView(this@VoteDetailsActivity)
                        textView.text = "${comment.commentAuthorUsername}: ${comment.commentBody}"
                        commentsContainer.addView(textView)
                    }
                }
            }
            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                Toast.makeText(this@VoteDetailsActivity, "Error loading comments: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

}