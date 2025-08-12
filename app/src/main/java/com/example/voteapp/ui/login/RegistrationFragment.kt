package com.example.voteapp.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.voteapp.R
import com.example.voteapp.data.model.RegisterDto
import com.example.voteapp.data.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationFragment : Fragment() {

    private lateinit var usernameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var confirmPasswordEdit: EditText
    private lateinit var registerButton: Button
    private lateinit var loginRedirect: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameEdit = view.findViewById(R.id.usernameEdit)
        emailEdit = view.findViewById(R.id.emailEdit)
        passwordEdit = view.findViewById(R.id.passwordEdit)
        confirmPasswordEdit = view.findViewById(R.id.confirmPasswordEdit)
        registerButton = view.findViewById(R.id.registerButton)
        loginRedirect = view.findViewById(R.id.loginRedirect)

        registerButton.setOnClickListener {
            registerUser()
        }

        loginRedirect.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    private fun registerUser() {

        val username = usernameEdit.text.toString()
        val email = emailEdit.text.toString()
        val password = passwordEdit.text.toString()
        val confirmPassword = confirmPasswordEdit.text.toString()


        if (username.isEmpty()) {
            usernameEdit.error = "Username needed"
            return }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.error = "Email is invalid"
            return
        }
        if (password.length < 8) {
            passwordEdit.error = "Minimum 8 characters"
            return
        }
        if (password != confirmPassword) {
            confirmPasswordEdit.error = "Passwords do not match"
            return
        }

        val api = RetrofitInstance.getApi(requireContext())
        val registerDto = RegisterDto(username, email, password)
        Log.e("VoteDetailsActivity", "Error decoding image: ${registerDto.toString()}")

        api.registerUser(registerDto).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){

                    Toast.makeText(
                        requireContext(),
                        "Account created, check email box to activate account.",
                        Toast.LENGTH_LONG
                    ).show()

                    parentFragmentManager.popBackStack()

                }
                else{
                    Toast.makeText(requireContext(), "Registration failed: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }


}