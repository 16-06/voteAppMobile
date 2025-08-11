//import androidx.compose.runtime.*
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import android.content.Context
//import androidx.compose.foundation.clickable
//import com.example.voteapp.data.api.VoteApi
//import com.example.voteapp.data.model.Vote
//import com.example.voteapp.ui.profile.UserProfileViewModel
//
//@Composable
//fun UserProfileScreen(
//    userId: Long,
//    currentUserId: Long,
//    api: VoteApi, // Retrofit API lub repozytorium
//    onEditProfile: () -> Unit,
//    onVoteClicked: (voteId: Long) -> Unit,
//) {
//    // ViewModel bez Hilt: trzymany na kluczu userId
//    val viewModel = remember(userId) {
//        UserProfileViewModel(userId, api)
//    }
//
//    val user = viewModel.user
//    val votes = viewModel.votes
//    val hasMore = viewModel.hasMore
//    val error = viewModel.errorMessage
//
//    // Error Snackbar
//    error?.let {
//        Snackbar(
//            modifier = Modifier.padding(16.dp),
//            action = {
//                TextButton(onClick = { viewModel.clearError() }) {
//                    Text("OK")
//                }
//            }
//        ) { Text(it) }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(20.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        // User Info Card
//        Card(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(text = "Username: ${user?.username ?: "..."}", style = MaterialTheme.typography.titleMedium)
//                Text(text = "Imię: ${user?.firstName ?: "..."}")
//                Text(text = "Nazwisko: ${user?.lastName ?: "..."}")
//                Text(text = "Opis: ${user?.bio ?: "..."}")
//                Text(text = "Zainteresowania: ${user?.interests ?: "..."}")
//            }
//        }
//
//        // Edit button only if current user is profile owner
//        if (userId == currentUserId) {
//            Button(onClick = onEditProfile) {
//                Text("Edytuj profil")
//            }
//        }
//
//        Spacer(Modifier.height(16.dp))
//        Text(
//            text = "Twoje głosowania:",
//            style = MaterialTheme.typography.titleSmall
//        )
//        LazyColumn(
//            modifier = Modifier.weight(1f),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(votes) { vote ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { onVoteClicked(vote.id) }
//                ) {
//                    Text(
//                        vote.name,
//                        modifier = Modifier.padding(10.dp),
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//            item {
//                if (hasMore) {
//                    Button(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 12.dp),
//                        onClick = { viewModel.fetchMoreVotes() }
//                    ) {
//                        Text("Załaduj więcej")
//                    }
//                } else if (votes.isNotEmpty()) {
//                    Text(
//                        "Brak kolejnych głosowań",
//                        modifier = Modifier.padding(16.dp),
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//            }
//        }
//    }
//}
