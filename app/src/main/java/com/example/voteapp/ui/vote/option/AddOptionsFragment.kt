package com.example.voteapp.ui.vote.option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.voteapp.R
import com.example.voteapp.data.model.OptionRequestDto
import com.example.voteapp.data.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddOptionsFragment: Fragment() {

    private var voteId: Int = -1
    private lateinit var optionInput: EditText
    private lateinit var addButton: Button
    private lateinit var optionsList: LinearLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        voteId = arguments?.getInt("voteId", -1) ?: -1

        if (voteId == -1) {
            Toast.makeText(requireContext(), "ID not found", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
            return
        }

        optionInput = view.findViewById(R.id.optionInput)
        addButton = view.findViewById(R.id.addOptionButton)
        optionsList = view.findViewById(R.id.optionsList)

        addButton.setOnClickListener {
            val optionText = optionInput.text.toString()
            if (optionText.isNotBlank()) {
                addOptionToVote(optionText)
            }
            else{
                Toast.makeText(requireContext(), "Enter Text", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun addOptionToVote(optionText: String, imageData: ByteArray? = null) {
        val api = RetrofitInstance.getApi(requireContext())

        val dto = OptionRequestDto(
            name = optionInput.text.toString(),
            imageData = imageData,
            voteId = voteId.toLong()
        )
        
        api.addVoteOption(dto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Toast.makeText(requireContext(), "Option Added", Toast.LENGTH_SHORT).show()
                    addOptionToView(dto.name)
                    optionInput.text.clear()
                } else {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun addOptionToView(optionName: String) {
        val textView = TextView(requireContext()).apply {
            text = optionName
            setPadding(16, 16, 16, 16)
            textSize = 18f
        }
        optionsList.addView(textView)
    }
}