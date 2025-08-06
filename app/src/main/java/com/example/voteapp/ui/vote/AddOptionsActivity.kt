package com.example.voteapp.ui.vote

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voteapp.R
import com.example.voteapp.data.model.OptionRequestDto
import com.example.voteapp.data.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddOptionsActivity: AppCompatActivity() {

    private var voteId: Long = -1
    private lateinit var optionInput: EditText
    private lateinit var addButton: Button
    private lateinit var optionsList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_options)

        voteId = intent.getLongExtra("voteId", -1)
        if (voteId == -1L) {

            Toast.makeText(this, "Błędne ID głosowania", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        optionInput = findViewById(R.id.optionInput)
        addButton = findViewById(R.id.addOptionButton)
        optionsList = findViewById(R.id.optionsList)

        addButton.setOnClickListener {
            val optionText = optionInput.text.toString()
            if (optionText.isNotBlank()) {
                addOptionToVote(optionText)
                Log.d("AddOption", "Kliknięto przycisk, dodaję opcję: $optionText")
            }
            else{
                Toast.makeText(this, "Wprowadź tekst opcji", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun addOptionToVote(optionText: String, imageData: ByteArray? = null) {
        val api = RetrofitInstance.getApi(this)

        val dto = OptionRequestDto(
            name = optionInput.text.toString(),
            imageData = imageData,
            voteId = voteId
        )
        
        api.addVoteOption(dto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Toast.makeText(this@AddOptionsActivity, "Opcja dodana", Toast.LENGTH_SHORT).show()
                    addOptionToView(dto.name)
                    optionInput.text.clear()
                } else {
                    Toast.makeText(this@AddOptionsActivity, "Błąd podczas dodawania opcji", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AddOptionsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun addOptionToView(optionName: String) {
        val textView = TextView(this).apply {
            text = optionName
            setPadding(16, 16, 16, 16)
            textSize = 18f
        }
        optionsList.addView(textView)
    }
}