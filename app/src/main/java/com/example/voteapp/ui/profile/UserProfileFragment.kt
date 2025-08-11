package com.example.voteapp.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.voteapp.R
import com.example.voteapp.data.model.UserDto
import com.example.voteapp.data.model.VoteResponseDto
import com.example.voteapp.data.network.RetrofitInstance
import com.example.voteapp.ui.vote.details.VoteDetailsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileFragment: Fragment(R.layout.fragment_user_profile) {

    private lateinit var usernameView: TextView
    private lateinit var firstNameView: TextView
    private lateinit var lastNameView: TextView
    private lateinit var bioView: TextView
    private lateinit var interestsView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var voteListLayout: LinearLayout
    private lateinit var loadMoreButton: Button
    private lateinit var prefs: SharedPreferences

    private var userId: Long = -1
    private var currentPage: Int = 0
    private val votesPerPage : Int = 9
    private var hasMore = true

    private val voteViews = mutableListOf<TextView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        userId = prefs.getLong("userId", -1)


        if (userId == -1L) {
            Toast.makeText(requireContext(), "ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        usernameView = view.findViewById(R.id.usernameTextView)
        firstNameView = view.findViewById(R.id.firstNameTextView)
        lastNameView = view.findViewById(R.id.lastNameTextView)
        bioView = view.findViewById(R.id.bioTextView)
        interestsView = view.findViewById(R.id.interestsTextView)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        voteListLayout = view.findViewById(R.id.voteListLayout)
        loadMoreButton = view.findViewById(R.id.loadMoreButton)

        fetchUserData()
        fetchVotes()

        loadMoreButton.setOnClickListener {
            if (hasMore) {
                currentPage++
                fetchVotes()
            } else {
                Toast.makeText(requireContext(), "No more votes", Toast.LENGTH_SHORT).show()
            }
        }

        editProfileButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun fetchVotes() {
        val api = RetrofitInstance.getApi(requireContext())

        api.getVotesByUser(userId,votesPerPage, currentPage).enqueue(object : Callback<List<VoteResponseDto>> {
            override fun onResponse(call: Call<List<VoteResponseDto>>, response: Response<List<VoteResponseDto>>) {
                if(response.isSuccessful){

                    val votes = response.body()!!
                    if(votes.size < votesPerPage){
                        hasMore = false
                        loadMoreButton.visibility = Button.GONE
                    }

                    for((index, vote) in votes.withIndex()) {
                        val voteView = TextView(requireContext()).apply {
                            text = vote.name
                            setOnClickListener {
                                val intent = Intent(requireContext(), VoteDetailsActivity::class.java)
                                intent.putExtra("voteId", vote.id)
                                startActivity(intent)
                            }
                        }
                        voteListLayout.addView(voteView)
                        voteViews.add(voteView)
                    }


                }
                else {
                    Toast.makeText(requireContext(), "Api error", Toast.LENGTH_SHORT).show()
                }


            }

            override fun onFailure(call: Call<List<VoteResponseDto>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }


        })
    }


    private fun fetchUserData() {
        val api = RetrofitInstance.getApi(requireContext())

        api.getPublicUserProfile(userId).enqueue(object : Callback<UserDto>{
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if(response.isSuccessful){
                    val user = response.body()!!
                    firstNameView.text = "Imię: ${user.firstName}"
                    lastNameView.text = "Nazwisko: ${user.lastName}"
                    bioView.text = "Opis: ${user.bio}"
                    interestsView.text = "Zainteresowania: ${user.interests}"

                    val currentUser = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getLong("userId", -1L)
                    if (currentUser != -1L && currentUser == userId) {
                        editProfileButton.visibility = Button.VISIBLE
                    } else {
                        editProfileButton.visibility = Button.GONE
                    }

                }
                else{
                    Toast.makeText(requireContext(), "Error fetching user data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}