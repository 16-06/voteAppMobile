package com.example.voteapp.ui.login

import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voteapp.R
import com.example.voteapp.data.model.LoginRequest
import com.example.voteapp.data.model.LoginResponse
import com.example.voteapp.data.network.RetrofitInstance
import com.example.voteapp.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.edit


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.usernameEditText)
        val passwordInput = findViewById<EditText>(R.id.passwordEditText)
        val loginButton   =  findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener{
            val loginRequest = LoginRequest(usernameInput.text.toString(),passwordInput.text.toString())


            RetrofitInstance.getApi(this).login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>){
                    if(response.isSuccessful){
                        val token = response.body()?.token
                        if (token!= null){
                            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            prefs.edit { putString("jwt_token", token) }
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                        else{
                            Toast.makeText(this@LoginActivity, "Token not found",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(this@LoginActivity, "Bad credentials",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error ${t.message}",Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}
