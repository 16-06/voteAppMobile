//package com.example.voteapp.ui.profile
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.voteapp.data.api.VoteApi
//import com.example.voteapp.data.model.UserDto
//import kotlinx.coroutines.launch
//
//class UserProfileViewModel(private val userId: Long, private val api: VoteApi): ViewModel() {
//
//    var user by mutableStateOf<UserDto?>(null)
//        private set
//    var errorMessages by mutableStateOf<String?>(null)
//        private set
//    var votes by mutableStateOf<List<String>?>(null)
//        private set
//    var page by mutableStateOf(1)
//        private set
//    var hasMore by mutableStateOf(true)
//        private set
//
//    private val votesPerPage = 9
//
//    init {
//        fetchUser()
//        fetchMoreVotes()
//    }
//
//    private fun fetchMoreVotes() {
//        viewModelScope.launch {
//            try {
//                val response = api.getVotesByUser(userId, votesPerPage, page)
//                if (response.isSuccessful) {
//                    val newVotes = response.body() ?: emptyList()
//                    votes = votes + newVotes
//                    if (newVotes.size < votesPerPage) {
//                        hasMore = false
//                    } else {
//                        page += 1
//                    }
//                } else {
//                    errorMessage = "Błąd pobierania głosów"
//                }
//
//            }catch (e: Exception){
//                errorMessages = e.localizedMessage ?: "Unknown error"
//            }
//        }
//    }
//
//    private fun fetchUser() {
//        viewModelScope.launch {
//            try {
//                val response = api.getPublicUserProfile(userId)
//                if (response.isSuccessful) {
//                    user = response.body()
//                } else {
//                    errorMessages = "Error fetching user: ${response.errorBody()?.string()}"
//                }
//            } catch (e: Exception){
//                errorMessages = e.localizedMessage ?: "Unknown error"
//            }
//
//        }
//    }
//    fun clearError() {
//        errorMessage = null
//    }
//}