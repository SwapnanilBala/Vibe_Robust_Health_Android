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
import com.robusthealth.android.data.model.MemberPlansResponse
import com.robusthealth.android.data.model.PlanRecord
import com.robusthealth.android.data.model.Resource
import com.robusthealth.android.ui.components.GradientButton
import com.robusthealth.android.ui.components.InfoCard
import com.robusthealth.android.ui.components.ScreenContainer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Composable
fun MemberLoginScreen(
    onLogin: (String) -> Unit,
    onBack: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    ScreenContainer(title = "Member Login", subtitle = "Access saved plans", onBack = onBack) {
        Column {
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            GradientButton(label = "Login", onClick = { onLogin(email.value) })
        }
    }
}

@Composable
fun MemberPlansScreen(
    state: Resource<MemberPlansResponse>,
    json: Json,
    onBack: () -> Unit
) {
    ScreenContainer(title = "Member Plans", subtitle = "History & coaches", onBack = onBack) {
        when (state) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Error -> Text(text = state.message, color = MaterialTheme.colorScheme.error)
            is Resource.Success -> {
                val member = state.data.member
                Text(text = "Member: ${member.email}")
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(state.data.plans) { plan ->
                        PlanCard(plan = plan, json = json)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanCard(plan: PlanRecord, json: Json) {
    InfoCard(
        title = "Week ${plan.weekStart ?: "N/A"}",
        body = plan.planJson?.let { json.encodeToString(JsonObject.serializer(), it) } ?: "No plan JSON",
        modifier = Modifier.padding(vertical = 6.dp)
    )
}
