package com.robusthealth.android.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robusthealth.android.data.model.Resource
import com.robusthealth.android.data.model.TrainerClientRecord
import com.robusthealth.android.ui.components.GradientButton
import com.robusthealth.android.ui.components.InfoCard
import com.robusthealth.android.ui.components.ScreenContainer

@Composable
fun TrainerLoginScreen(
    onLogin: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val credential = remember { mutableStateOf("") }
    ScreenContainer(title = "Trainer Login", subtitle = "Credentialed access", onBack = onBack) {
        Column {
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = credential.value,
                onValueChange = { credential.value = it },
                label = { Text("Credential") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            GradientButton(label = "Login", onClick = { onLogin(email.value, credential.value) })
        }
    }
}

@Composable
fun TrainerClientsScreen(
    state: Resource<List<TrainerClientRecord>>,
    onBack: () -> Unit
) {
    ScreenContainer(title = "Trainer Clients", subtitle = "Assignments", onBack = onBack) {
        when (state) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Error -> Text(text = state.message, color = MaterialTheme.colorScheme.error)
            is Resource.Success -> {
                LazyColumn {
                    items(state.data) { client ->
                        InfoCard(
                            title = client.email ?: client.memberId,
                            body = buildString {
                                appendLine("Support: ${client.supportArea ?: "N/A"}")
                                appendLine("Package: ${client.packageTier ?: "basic"}")
                                appendLine("Goal: ${client.goal ?: "unknown"}")
                                appendLine("Days/Week: ${client.daysPerWeek ?: 0}")
                                append("Equipment: ${client.equipment ?: ""}")
                            },
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
