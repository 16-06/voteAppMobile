package com.example.voteapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voteapp.R
import com.example.voteapp.data.model.UserDto
import com.example.voteapp.data.model.VoteResponseDto
import com.example.voteapp.data.network.RetrofitInstance
import com.example.voteapp.ui.vote.details.VoteDetailsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileActivity: AppCompatActivity() {

    private lateinit var usernameView: TextView
    private lateinit var firstNameView: TextView
    private lateinit var lastNameView: TextView
    private lateinit var bioView: TextView
    private lateinit var interestsView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var voteListLayout: LinearLayout
    private lateinit var loadMoreButton: Button

    private var userId: Long = -1
    private var currentPage: Int = 0
    private val votesPerPage : Int = 9
    private var hasMore = true

    private val voteViews = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        userId = getSharedPreferences("app_prefs", MODE_PRIVATE).getLong("userId", -1)
        Log.d("VoteApp", "User authenticated: $userId")

        if (userId == -1L) {
            Toast.makeText(this, "ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        usernameView = findViewById(R.id.usernameTextView)
        firstNameView = findViewById(R.id.firstNameTextView)
        lastNameView = findViewById(R.id.lastNameTextView)
        bioView = findViewById(R.id.bioTextView)
        interestsView = findViewById(R.id.interestsTextView)
        editProfileButton = findViewById(R.id.editProfileButton)
        voteListLayout = findViewById(R.id.voteListLayout)
        loadMoreButton = findViewById(R.id.loadMoreButton)

        fetchUserData()
        fetchVotes()

        loadMoreButton.setOnClickListener {
            if (hasMore) {
                currentPage++
                fetchVotes()
            } else {
                Toast.makeText(this, "No more votes", Toast.LENGTH_SHORT).show()
            }
        }

        editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }

    private fun fetchVotes() {
        val api = RetrofitInstance.getApi(this)

        userId = getSharedPreferences("app_prefs", MODE_PRIVATE).getLong("userId", -1)

        api.getVotesByUser(userId,votesPerPage, currentPage).enqueue(object : Callback<List<VoteResponseDto>> {
            override fun onResponse(call: Call<List<VoteResponseDto>>, response: Response<List<VoteResponseDto>>) {
                if(response.isSuccessful){

                    val votes = response.body()!!
                    if(votes.size < votesPerPage){
                        hasMore = false
                        loadMoreButton.visibility = Button.GONE
                    }

                    for((index, vote) in votes.withIndex()) {
                        val voteView = TextView(this@UserProfileActivity).apply {
                            text = vote.name
                            setOnClickListener {
                                val intent = Intent(this@UserProfileActivity, VoteDetailsActivity::class.java)
                                intent.putExtra("voteId", vote.id)
                                startActivity(intent)
                            }
                        }
                        voteListLayout.addView(voteView)
                        voteViews.add(voteView)
                    }


                }
                else{
                    Toast.makeText(this@UserProfileActivity, "Api error", Toast.LENGTH_SHORT).show()
                }


            }

            override fun onFailure(call: Call<List<VoteResponseDto>>, t: Throwable) {
                Toast.makeText(this@UserProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }


        })
    }


    private fun fetchUserData() {
        val api = RetrofitInstance.getApi(this)
        userId = getSharedPreferences("app_prefs", MODE_PRIVATE).getLong("userId", -1)

        api.getPublicUserProfile(userId).enqueue(object : Callback<UserDto>{
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if(response.isSuccessful){
                    val user = response.body()!!
                    firstNameView.text = "Imię: ${user.firstName}"
                    lastNameView.text = "Nazwisko: ${user.lastName}"
                    bioView.text = "Opis: ${user.bio}"
                    interestsView.text = "Zainteresowania: ${user.interests}"

                    val currentUser = getSharedPreferences("app_prefs", MODE_PRIVATE).getLong("userId", -1L)
                    if (currentUser != -1L && currentUser == userId) {
                        editProfileButton.visibility = Button.VISIBLE
                    } else {
                        editProfileButton.visibility = Button.GONE
                    }

                }
                else{
                    Toast.makeText(this@UserProfileActivity, "Error fetching user data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Toast.makeText(this@UserProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}