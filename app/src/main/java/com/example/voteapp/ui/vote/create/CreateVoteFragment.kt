package com.example.voteapp.ui.vote.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.voteapp.R
import com.example.voteapp.data.model.VoteResponseDto
import com.example.voteapp.data.network.RetrofitInstance
import com.example.voteapp.ui.vote.option.AddOptionsFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateVoteFragment : Fragment() {

    private lateinit var voteNameInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var createVoteButton: Button

    private val categories = listOf("lifestyle", "sport", "polityka", "zabawne", "motoryzacja", "informatyka")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_vote, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        voteNameInput =  view.findViewById<EditText>(R.id.voteNameInput)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        createVoteButton = view.findViewById(R.id.createVoteButton)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
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
            Toast.makeText(requireContext(), "Name needed", Toast.LENGTH_SHORT).show()
            return
        }

        val voteData = mapOf(
            "name" to name,
            "category" to category
        )
        val api = RetrofitInstance.getApi(requireContext())

        api.createVote(voteData).enqueue(object : Callback<VoteResponseDto> {
            override fun onResponse(call: Call<VoteResponseDto>, response: Response<VoteResponseDto>){
                if(response.isSuccessful){

                    val vote = response.body()!!
                    Toast.makeText(requireContext(), "Vote created: ${vote.name}", Toast.LENGTH_SHORT).show()

                    val fragment = AddOptionsFragment().apply {
                        arguments = Bundle().apply {
                            putInt("voteId", vote.id.toInt())
                        }
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                else {
                    Toast.makeText(requireContext(), "Error creating vote: ${response.message()}", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onFailure(call: Call<VoteResponseDto>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

}