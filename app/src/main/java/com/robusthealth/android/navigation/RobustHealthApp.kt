package com.robusthealth.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.robusthealth.android.ui.screens.AboutScreen
import com.robusthealth.android.ui.screens.CheckoutScreen
import com.robusthealth.android.ui.screens.HomeScreen
import com.robusthealth.android.ui.screens.MemberLoginScreen
import com.robusthealth.android.ui.screens.MemberPlansScreen
import com.robusthealth.android.ui.screens.OnboardingScreen
import com.robusthealth.android.ui.screens.PlanScreen
import com.robusthealth.android.ui.screens.PricingScreen
import com.robusthealth.android.ui.screens.TrainerClientsScreen
import com.robusthealth.android.ui.screens.TrainerLoginScreen
import kotlinx.serialization.json.Json

private val AppJson = Json { prettyPrint = true; ignoreUnknownKeys = true }

@Composable
fun RobustHealthApp(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinations.Home.route) {
        composable(Destinations.Home.route) {
            HomeScreen(
                onStartOnboarding = { navController.navigate(Destinations.Onboarding.route) { launchSingleTop = true } },
                onViewHowItWorks = { navController.navigate(Destinations.About.route) { launchSingleTop = true } },
                onMemberLogin = { navController.navigate(Destinations.MemberLogin.route) { launchSingleTop = true } },
                onTrainerLogin = { navController.navigate(Destinations.TrainerLogin.route) { launchSingleTop = true } },
                onPricing = { navController.navigate(Destinations.Pricing.route) { launchSingleTop = true } },
                onAbout = { navController.navigate(Destinations.About.route) { launchSingleTop = true } }
            )
        }
        composable(Destinations.Onboarding.route) {
            OnboardingScreen(
                onSubmit = {
                    viewModel.submitProfile(it)
                    navController.navigate(Destinations.Plan.route) { launchSingleTop = true }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.Plan.route) {
            val planState by viewModel.planState.collectAsState()
            val profileId by viewModel.profileId.collectAsState()
            PlanScreen(
                planState = planState,
                json = AppJson,
                onGenerate = {
                    profileId?.let { viewModel.generatePlan(it) }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.MemberLogin.route) {
            MemberLoginScreen(
                onLogin = {
                    viewModel.memberLogin(it)
                    navController.navigate(Destinations.MemberPlans.route) { launchSingleTop = true }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.MemberPlans.route) {
            val memberState by viewModel.memberState.collectAsState()
            MemberPlansScreen(
                state = memberState,
                json = AppJson,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.TrainerLogin.route) {
            TrainerLoginScreen(
                onLogin = { email, credential ->
                    viewModel.trainerLogin(email, credential)
                    navController.navigate(Destinations.TrainerClients.route) { launchSingleTop = true }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.TrainerClients.route) {
            val trainerState by viewModel.trainerState.collectAsState()
            TrainerClientsScreen(
                state = trainerState,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.Pricing.route) {
            PricingScreen(onBack = { navController.popBackStack() })
        }
        composable(Destinations.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable(Destinations.Checkout.route) {
            CheckoutScreen(onBack = { navController.popBackStack() })
        }
    }
}
