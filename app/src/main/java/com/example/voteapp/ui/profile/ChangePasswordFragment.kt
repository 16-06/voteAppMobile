package com.example.voteapp.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.voteapp.R
import com.example.voteapp.data.model.ChangePasswordDto
import com.example.voteapp.data.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragment : Fragment() {

    private lateinit var oldPasswordInput: EditText
    private lateinit var newPasswordInput: EditText
    private lateinit var saveButton: Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        oldPasswordInput = view.findViewById(R.id.editTextOldPassword)
        newPasswordInput = view.findViewById(R.id.editTextNewPassword)
        saveButton = view.findViewById(R.id.buttonSavePassword)

        saveButton.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val oldPassword = oldPasswordInput.text.toString()
        val newPassword = newPasswordInput.text.toString()

        if(oldPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Enter old and new password", Toast.LENGTH_SHORT).show()
            return
        }

        val api = RetrofitInstance.getApi(requireContext())

        val changePasswordRequest = ChangePasswordDto(
            password = oldPassword,
            newPassword = newPassword
        )

        api.changePassword(changePasswordRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                    oldPasswordInput.text.clear()
                    newPasswordInput.text.clear()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error changing password", Toast.LENGTH_SHORT).show()
            }

        })


    }
}