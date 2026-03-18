package com.robusthealth.android.navigation

sealed class Destinations(val route: String) {
    data object Home : Destinations("home")
    data object Onboarding : Destinations("onboarding")
    data object Plan : Destinations("plan")
    data object MemberLogin : Destinations("member_login")
    data object MemberPlans : Destinations("member_plans")
    data object TrainerLogin : Destinations("trainer_login")
    data object TrainerClients : Destinations("trainer_clients")
    data object Pricing : Destinations("pricing")
    data object About : Destinations("about")
    data object Checkout : Destinations("checkout")
}
