package com.robusthealth.android.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.robusthealth.android.data.model.ProfilePayload
import com.robusthealth.android.ui.components.GradientButton
import com.robusthealth.android.ui.components.ScreenContainer

@Composable
fun OnboardingScreen(
    onSubmit: (ProfilePayload) -> Unit,
    onBack: () -> Unit
) {
    var age by rememberSaveable { mutableStateOf("28") }
    var sex by rememberSaveable { mutableStateOf("male") }
    var height by rememberSaveable { mutableStateOf("180") }
    var weight by rememberSaveable { mutableStateOf("82") }
    var goal by rememberSaveable { mutableStateOf("muscle_gain") }
    var days by rememberSaveable { mutableStateOf("4") }
    var minutes by rememberSaveable { mutableStateOf("60") }
    var activity by rememberSaveable { mutableStateOf("moderate") }
    var equipment by rememberSaveable { mutableStateOf("gym") }
    var limitations by rememberSaveable { mutableStateOf("") }
    var diet by rememberSaveable { mutableStateOf("") }
    var allergies by rememberSaveable { mutableStateOf("") }
    var sleep by rememberSaveable { mutableStateOf("7.5") }
    var stress by rememberSaveable { mutableStateOf("moderate") }

    val numericRequiredValid = listOf(age, height, weight, days, minutes).all { it.toIntOrNull()?.let { n -> n > 0 } == true }
    val isValid = numericRequiredValid && sex.isNotBlank() && goal.isNotBlank() && activity.isNotBlank() && equipment.isNotBlank()

    ScreenContainer(title = "Onboarding", subtitle = "Profile setup", onBack = onBack) {
        Column(modifier = Modifier.padding(4.dp).verticalScroll(rememberScrollState())) {
            Field("Age", age, KeyboardType.Number) { age = it }
            Field("Sex at birth", sex) { sex = it }
            Field("Height (cm)", height, KeyboardType.Number) { height = it }
            Field("Weight (kg)", weight, KeyboardType.Number) { weight = it }
            Field("Goal", goal) { goal = it }
            Field("Days per week", days, KeyboardType.Number) { days = it }
            Field("Session minutes", minutes, KeyboardType.Number) { minutes = it }
            Field("Activity level", activity) { activity = it }
            Field("Equipment", equipment) { equipment = it }
            Field("Limitations", limitations) { limitations = it }
            Field("Diet preference", diet) { diet = it }
            Field("Allergies", allergies) { allergies = it }
            Field("Sleep hours", sleep, KeyboardType.Number) { sleep = it }
            Field("Stress level", stress) { stress = it }

            Spacer(modifier = Modifier.height(12.dp))
            GradientButton(label = "Generate plan", enabled = isValid) {
                val payload = ProfilePayload(
                    age = age.toInt(),
                    sexAtBirth = sex,
                    heightCm = height.toInt(),
                    weightKg = weight.toInt(),
                    goal = goal,
                    daysPerWeek = days.toInt(),
                    sessionMinutes = minutes.toInt(),
                    activityLevel = activity,
                    equipment = equipment,
                    limitations = limitations.ifBlank { null },
                    dietPreference = diet.ifBlank { null },
                    allergies = allergies.ifBlank { null },
                    sleepHours = sleep.toDoubleOrNull(),
                    stressLevel = stress.ifBlank { null }
                )
                onSubmit(payload)
            }
        }
    }
}

@Composable
private fun Field(label: String, value: String, keyboardType: KeyboardType = KeyboardType.Text, onChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label)
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}
