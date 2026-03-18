package com.robusthealth.android.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robusthealth.android.ui.components.ScreenContainer

@Composable
fun PricingScreen(onBack: () -> Unit) {
    ScreenContainer(title = "Pricing", subtitle = "Packages", onBack = onBack) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(text = "Basic: $119/mo · $1,190/yr")
            Text(text = "Pro: $149/mo · $1,490/yr")
            Text(text = "Ultimate: $179/mo · $1,790/yr")
        }
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    ScreenContainer(title = "About", subtitle = "System overview", onBack = onBack) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text("Intake → Plan → View → Regenerate")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Evidence-based workout splits, macro targets, and sleep recommendations with glassmorphic UI and hero videos.")
        }
    }
}

@Composable
fun CheckoutScreen(onBack: () -> Unit) {
    ScreenContainer(title = "Checkout", subtitle = "Select a package", onBack = onBack) {
        Text("Integrate your payment provider or redirect to web checkout.")
    }
}
