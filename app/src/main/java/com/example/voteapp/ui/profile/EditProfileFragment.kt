package com.example.voteapp.ui.profile

import android.content.Context
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
import com.example.voteapp.data.model.UpdateProfileDto
import com.example.voteapp.data.model.UserDto
import com.example.voteapp.data.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment: Fragment() {

    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editBio: EditText
    private lateinit var editInterests: EditText
    private lateinit var saveButton: Button
    private lateinit var prefs: SharedPreferences

    private var userId: Long = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        userId = prefs.getLong("userId", -1)

        editFirstName = view.findViewById(R.id.editFirstName)
        editLastName = view.findViewById(R.id.editLastName)
        editBio = view.findViewById(R.id.editBio)
        editInterests = view.findViewById(R.id.editInterests)
        saveButton = view.findViewById(R.id.saveButton)

        fetchUserProfile()

        saveButton.setOnClickListener {
            updateUserProfile()
        }

        return view

    }

    private fun updateUserProfile() {

        val api = RetrofitInstance.getApi(requireContext())
        val updateData = UpdateProfileDto(
            firstName = editFirstName.text.toString(),
            lastName = editLastName.text.toString(),
            bio = editBio.text.toString(),
            interests = editInterests.text.toString()

            )

        api.updateProfile(updateData).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>){

                if(response.isSuccessful){
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                else{
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error updating profile", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun fetchUserProfile() {
        val api = RetrofitInstance.getApi(requireContext())

        api.getPublicUserProfile(userId).enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if(response.isSuccessful){
                    val profile = response.body()

                    if(profile != null) {
                        editFirstName.setText(profile.firstName)
                        editLastName.setText(profile.lastName)
                        editBio.setText(profile.bio)
                        editInterests.setText(profile.interests)


                }
            }

        }
            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Toast.makeText(requireContext(), "Error fetching user data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
