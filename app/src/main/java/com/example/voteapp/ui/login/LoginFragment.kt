package com.example.voteapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.voteapp.R
import com.example.voteapp.data.model.LoginRequest
import com.example.voteapp.data.network.RetrofitInstance
import com.example.voteapp.ui.main.MainActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.voteapp.R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val usernameInput = view.findViewById<EditText>(R.id.usernameEditText)
        val passwordInput = view.findViewById<EditText>(R.id.passwordEditText)
        val loginButton   =  view.findViewById<Button>(R.id.loginButton)
        val registerButton = view.findViewById<Button>(R.id.registerButton)

        loginButton.setOnClickListener{
            val loginRequest = LoginRequest(usernameInput.text.toString(),passwordInput.text.toString())


            RetrofitInstance.getApi(requireContext()).login(loginRequest).enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){

                    Log.d("VoteApp", "Votes: ${response.code()}")

                    if(response.code() == 202){
                        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit { putString("username_2fa", usernameInput.text.toString()) }
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, TwoFactorCodeFragment())
                            .addToBackStack(null)
                            .commit()

                    }

                    else if(response.isSuccessful){
                        val token = response.body()?.string()?.trim('"')
                        if (token!= null){
                            val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            prefs.edit { putString("jwt_token", token) }
                            startActivity(Intent(requireContext(), MainActivity::class.java))

                        }
                        else{
                            Toast.makeText(requireContext(), "Token not found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    else{
                        Toast.makeText(requireContext(), "Bad credentials", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }

        registerButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegistrationFragment())
                .addToBackStack(null)
                .commit()
        }
    }

}