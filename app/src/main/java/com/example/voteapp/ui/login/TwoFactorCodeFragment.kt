package com.example.voteapp.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.voteapp.R
import com.example.voteapp.data.model.TwoFactorLoginDto
import com.example.voteapp.data.network.RetrofitInstance
import com.example.voteapp.ui.main.MainActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TwoFactorCodeFragment : Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activation_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val codeInput = view.findViewById<EditText>(R.id.codeEditText)
        val verifyButton = view.findViewById<Button>(R.id.verifyButton)
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username_2fa", null)


        verifyButton.setOnClickListener {
            val code = codeInput.text.toString()

            if (code.isEmpty()){
                Toast.makeText(requireContext(), "Enter activation code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val api = RetrofitInstance.getApi(requireContext())
            val request = TwoFactorLoginDto(
                username = username ?: "",
                code = code
            )

            api.verify2FA(request).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if(response.isSuccessful){

                        val token = response.body()?.string()?.trim('"')

                        if(token!= null){
                            prefs.edit().apply(){
                                putString("jwt_token", token)
                                remove("username_2fa")
                                apply()
                            }
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }
                    }
                    else{
                        Toast.makeText(requireContext(), "Invalid code", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }

    }
}