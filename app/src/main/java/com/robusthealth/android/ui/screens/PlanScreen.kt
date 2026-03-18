package com.robusthealth.android.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.robusthealth.android.data.model.PlanRecord
import com.robusthealth.android.data.model.Resource
import com.robusthealth.android.ui.components.GradientButton
import com.robusthealth.android.ui.components.ScreenContainer
import com.robusthealth.android.ui.components.ShimmerPlaceholder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Composable
fun PlanScreen(
    planState: Resource<PlanRecord?>,
    onGenerate: () -> Unit,
    onBack: () -> Unit,
    json: Json
) {
    ScreenContainer(title = "Plan", subtitle = "Workout · Nutrition · Sleep", onBack = onBack) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            when (planState) {
                is Resource.Loading -> {
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(18.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(140.dp), cornerRadius = 16.dp)
                }
                is Resource.Error -> Text(text = planState.message, color = MaterialTheme.colorScheme.error)
                is Resource.Success -> {
                    val plan = planState.data
                    if (plan == null) {
                        Text("No plan found yet. Generate one to begin.")
                    } else {
                        Text(
                            text = "Week start: ${plan.weekStart ?: "N/A"}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SelectionContainer {
                            Text(
                                text = plan.planJson?.let { json.encodeToString(JsonObject.serializer(), it) } ?: "(No plan_json)",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth().padding(4.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            GradientButton(label = "Generate / Regenerate", onClick = onGenerate)
        }
    }
}
