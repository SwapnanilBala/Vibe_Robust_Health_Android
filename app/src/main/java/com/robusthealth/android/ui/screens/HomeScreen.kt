package com.robusthealth.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.robusthealth.android.ui.components.GradientButton
import com.robusthealth.android.ui.components.InfoCard
import com.robusthealth.android.ui.components.ScreenContainer
import com.robusthealth.android.ui.theme.Fuchsia
import com.robusthealth.android.ui.theme.Indigo
import com.robusthealth.android.ui.theme.Violet

@Composable
fun HomeScreen(
    onStartOnboarding: () -> Unit,
    onViewHowItWorks: () -> Unit,
    onMemberLogin: () -> Unit,
    onTrainerLogin: () -> Unit,
    onPricing: () -> Unit,
    onAbout: () -> Unit
) {
    ScreenContainer(title = "Robust Health", subtitle = "Semi-automated fitness planning") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            HeroSection(onStartOnboarding = onStartOnboarding, onViewHowItWorks = onViewHowItWorks)

            InfoCard(
                title = "Member Portal",
                body = "Log in to retrieve saved plans, download PDFs, and browse the coach directory.")

            InfoCard(
                title = "Trainer Portal",
                body = "Credentialed trainers can view assigned clients, package tiers, and support areas.")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                GradientButton(label = "Member login", onClick = onMemberLogin, modifier = Modifier.weight(1f))
                GradientButton(label = "Trainer login", onClick = onTrainerLogin, modifier = Modifier.weight(1f))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                GradientButton(label = "Pricing", onClick = onPricing, modifier = Modifier.weight(1f))
                GradientButton(label = "About", onClick = onAbout, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun HeroSection(
    onStartOnboarding: () -> Unit,
    onViewHowItWorks: () -> Unit
) {
    BoxWithConstraints {
        val isCompact = maxWidth < 720.dp
        val spacing = if (isCompact) 16.dp else 24.dp
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(colors = listOf(Fuchsia, Violet, Indigo)),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(spacing)
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Indigo.copy(alpha = 0.35f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                if (isCompact) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
                        HeroTextBlock(onStartOnboarding, onViewHowItWorks)
                        HeroCards(isCompact = true)
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing), modifier = Modifier.fillMaxWidth()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) { HeroTextBlock(onStartOnboarding, onViewHowItWorks) }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f)
                        ) { HeroCards(isCompact = false) }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroTextBlock(onStartOnboarding: () -> Unit, onViewHowItWorks: () -> Unit) {
    AssistChip(
        onClick = {},
        label = { Text("Adaptive plans") },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color.White.copy(alpha = 0.18f),
            labelColor = Color.White
        )
    )
    Text(
        text = "Weekly systems that adapt",
        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
        color = MaterialTheme.colorScheme.onPrimary
    )
    Text(
        text = "Onboard in four steps, generate a plan, and revisit through member or trainer portals.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        GradientButton(label = "Start onboarding", onClick = onStartOnboarding, modifier = Modifier.weight(1f))
        GradientButton(label = "How it works", onClick = onViewHowItWorks, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HeroCards(isCompact: Boolean) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(if (isCompact) 1f else 0.88f)
            .padding(top = 6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White.copy(alpha = 0.12f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Recovery-ready", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
            Text("Auto-balances volume and deloads weekly.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f))
        }
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(if (isCompact) 1f else 0.72f)
            .padding(start = if (isCompact) 0.dp else 12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Trainer handoff", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
            Text("Export PDFs + assign in one tap.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f))
        }
    }
}
