package com.example.voteapp.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voteapp.R
import com.example.voteapp.data.model.UpdateProfileDto
import com.example.voteapp.data.model.UserDto
import com.example.voteapp.data.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity: AppCompatActivity() {

    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editBio: EditText
    private lateinit var editInterests: EditText
    private lateinit var saveButton: Button
    private lateinit var prefs: SharedPreferences

    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        userId = prefs.getLong("userId", -1)

        editFirstName = findViewById(R.id.editFirstName)
        editLastName = findViewById(R.id.editLastName)
        editBio = findViewById(R.id.editBio)
        editInterests = findViewById(R.id.editInterests)
        saveButton = findViewById(R.id.saveButton)

        fetchUserProfile()

        saveButton.setOnClickListener {
            updateUserProfile()
        }



    }

    private fun updateUserProfile() {

        val api = RetrofitInstance.getApi(this)
        val updateData = UpdateProfileDto(
            firstName = editFirstName.text.toString(),
            lastName = editLastName.text.toString(),
            bio = editBio.text.toString(),
            interests = editInterests.text.toString()

            )

        api.updateProfile(updateData).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>){

                if(response.isSuccessful){
                    Toast.makeText(this@EditProfileActivity, "Profile updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    Toast.makeText(this@EditProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EditProfileActivity, "Error updating profile", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun fetchUserProfile() {
        val api = RetrofitInstance.getApi(this)

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
                Toast.makeText(this@EditProfileActivity, "Error fetching user data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
