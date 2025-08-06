package com.example.voteapp.ui.vote

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voteapp.R
import com.example.voteapp.data.api.VoteApi
import com.example.voteapp.data.model.AuthenticatedUserDto
import com.example.voteapp.data.model.Comment
import com.example.voteapp.data.model.CommentRequestDto
import com.example.voteapp.data.model.CommentResponseDto
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
    private var currentUserId: Long = -1
    private var currentUserUsername: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_details)
        api = RetrofitInstance.getApi(this)

        api.getAuthenticatedUser().enqueue(object : Callback<AuthenticatedUserDto>{
            override fun onResponse(call: Call<AuthenticatedUserDto>, response: Response<AuthenticatedUserDto>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if( user != null) {
                        currentUserId = user.id
                        currentUserUsername = user.username
                    } else {
                        Toast.makeText(this@VoteDetailsActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AuthenticatedUserDto>, t: Throwable) {
                Toast.makeText(this@VoteDetailsActivity, "Error status: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

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
                        val imageView = optionView.findViewById<ImageView>(R.id.optionImage)

                        if(!option.imageData.isNullOrBlank()){
                            try {
                                val imageBytes = Base64.decode(option.imageData, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                if(bitmap != null){
                                    imageView.setImageBitmap(bitmap)
                                } else {
                                    imageView.setImageResource(R.drawable.ic_launcher_foreground)
                                }
                            } catch (e: Exception) {
                                Log.e("VoteDetailsActivity", "Error decoding image: ${e.message}")
                                imageView.setImageResource(R.drawable.ic_launcher_foreground)
                            }
                        } else {
                            imageView.setImageResource(R.drawable.ic_launcher_foreground)
                        }


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

        val commentRequest = CommentRequestDto(
            commentBody = commentText,
            voteId = voteId.toLong()
        )

        api.postComment(commentRequest).enqueue(object : Callback<CommentResponseDto> {
            override fun onResponse(call: Call<CommentResponseDto>, response: Response<CommentResponseDto>) {
                if(response.isSuccessful){
                    commentInput.text.clear()
                    loadComments()
                }
                else{
                    Toast.makeText(this@VoteDetailsActivity, "Failed to post comment", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CommentResponseDto>, t: Throwable) {
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
                        val commentView = layoutInflater.inflate(R.layout.item_comment, commentsContainer, false)

                        val commentText = commentView.findViewById<TextView>(R.id.commentText)
                        val deleteButton = commentView.findViewById<ImageView>(R.id.deleteCommentButton)

                        commentText.text = "${comment.commentAuthorUsername}: ${comment.commentBody}"

                        Log.d("comment", "commentData: ${comment.commentAuthorUsername}")
                        Log.d("user", "currentUser: ${currentUserUsername}")
                        Log.d("user", "userid: ${currentUserId}")

                        if (comment.commentAuthorUsername == currentUserUsername) {
                            deleteButton.visibility = View.VISIBLE
                            deleteButton.setOnClickListener {
                                deleteComment(comment.id.toInt())
                            }
                        } else {
                            deleteButton.visibility = View.GONE
                        }

                        commentsContainer.addView(commentView)
                    }
                }
            }
            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                Toast.makeText(this@VoteDetailsActivity, "Error loading comments: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun deleteComment(commentId : Int){
        api.deleteComment(commentId).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Toast.makeText(this@VoteDetailsActivity, "Comment deleted", Toast.LENGTH_SHORT).show()
                    loadComments()
                } else {
                    Toast.makeText(this@VoteDetailsActivity, "Failed to delete comment", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@VoteDetailsActivity, "Error deleting comment: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}