package com.agrisense.app.navigation

/**
 * All screen routes in the AgriSense navigation graph.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object FarmSetup : Screen("farm_setup")
    object Dashboard : Screen("dashboard")
    object SoilAnalysis : Screen("soil_analysis")
    object Weather : Screen("weather")
    object CropRecommendations : Screen("crop_recommendations")
    object CropDetail : Screen("crop_detail/{cropId}") {
        fun createRoute(cropId: String) = "crop_detail/$cropId"
    }
    object CropComparison : Screen("crop_comparison")
    object WhatIf : Screen("what_if")
    object ScenarioResults : Screen("scenario_results")
    object LongTermPlan : Screen("long_term_plan")
    object MarketInsights : Screen("market_insights")
    object Sensors : Screen("sensors")
    object Alerts : Screen("alerts")
    object Profile : Screen("profile")
    object FarmHistory : Screen("farm_history")
    object FarmReport : Screen("farm_report")
}
