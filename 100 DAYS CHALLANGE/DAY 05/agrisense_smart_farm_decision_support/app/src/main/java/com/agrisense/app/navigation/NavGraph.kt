package com.agrisense.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.agrisense.app.ui.screens.comparison.CropComparisonScreen
import com.agrisense.app.ui.screens.cropdetail.CropDetailScreen
import com.agrisense.app.ui.screens.crops.CropRecommendationsScreen
import com.agrisense.app.ui.screens.dashboard.DashboardScreen
import com.agrisense.app.ui.screens.farmsetup.FarmSetupScreen
import com.agrisense.app.ui.screens.longtermplan.LongTermPlanScreen
import com.agrisense.app.ui.screens.onboarding.OnboardingScreen
import com.agrisense.app.ui.screens.report.FarmReportScreen
import com.agrisense.app.ui.screens.scenario.ScenarioResultsScreen
import com.agrisense.app.ui.screens.soil.SoilAnalysisScreen
import com.agrisense.app.ui.screens.splash.SplashScreen
import com.agrisense.app.ui.screens.weather.WeatherScreen
import com.agrisense.app.ui.screens.whatif.WhatIfScreen
import com.agrisense.app.ui.screens.market.MarketInsightsScreen
import com.agrisense.app.ui.screens.sensors.SensorScreen
import com.agrisense.app.ui.screens.alerts.AlertsScreen
import com.agrisense.app.ui.screens.profile.ProfileScreen
import com.agrisense.app.ui.screens.history.FarmHistoryScreen
import com.agrisense.app.ui.viewmodels.MainViewModel

@Composable
fun AgriSenseNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Screen.FarmSetup.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onExploreDemo = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.FarmSetup.route) {
            FarmSetupScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAnalyzeFarm = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.FarmSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToSoil = { navController.navigate(Screen.SoilAnalysis.route) },
                onNavigateToWeather = { navController.navigate(Screen.Weather.route) },
                onNavigateToCrops = { navController.navigate(Screen.CropRecommendations.route) },
                onNavigateToMarket = { navController.navigate(Screen.MarketInsights.route) },
                onNavigateToSensors = { navController.navigate(Screen.Sensors.route) },
                onNavigateToAlerts = { navController.navigate(Screen.Alerts.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onEditFarm = { navController.navigate(Screen.FarmSetup.route) }
            )
        }

        composable(Screen.SoilAnalysis.route) {
            SoilAnalysisScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Weather.route) {
            WeatherScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CropRecommendations.route) {
            CropRecommendationsScreen(
                viewModel = viewModel,
                onCropDetail = { cropId ->
                    navController.navigate(Screen.CropDetail.createRoute(cropId))
                },
                onCompare = { navController.navigate(Screen.CropComparison.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CropDetail.route,
            arguments = listOf(navArgument("cropId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cropId = backStackEntry.arguments?.getString("cropId") ?: "maize"
            CropDetailScreen(
                viewModel = viewModel,
                cropId = cropId,
                onBack = { navController.popBackStack() },
                onCompare = { navController.navigate(Screen.CropComparison.route) },
                onWhatIf = { navController.navigate(Screen.WhatIf.route) }
            )
        }

        composable(Screen.CropComparison.route) {
            CropComparisonScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onWhatIf = { navController.navigate(Screen.WhatIf.route) },
                onSelectForPlan = { navController.navigate(Screen.LongTermPlan.route) }
            )
        }

        composable(Screen.WhatIf.route) {
            WhatIfScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onViewScenario = { navController.navigate(Screen.ScenarioResults.route) },
                onViewPlan = { navController.navigate(Screen.LongTermPlan.route) }
            )
        }

        composable(Screen.ScenarioResults.route) {
            ScenarioResultsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onViewPlan = { navController.navigate(Screen.LongTermPlan.route) }
            )
        }

        composable(Screen.LongTermPlan.route) {
            LongTermPlanScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onViewReport = { navController.navigate(Screen.FarmReport.route) }
            )
        }

        composable(Screen.FarmReport.route) {
            FarmReportScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onBackToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MarketInsights.route) {
            MarketInsightsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Sensors.route) {
            SensorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Alerts.route) {
            AlertsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAction = { alert ->
                    val destination = when (alert.id) {
                        "alert_1" -> Screen.SoilAnalysis.route
                        "alert_2" -> Screen.Weather.route
                        "alert_3" -> Screen.CropRecommendations.route
                        "alert_4" -> Screen.Sensors.route
                        else -> throw IllegalArgumentException(
                            "No demo destination configured for alert ${alert.id}"
                        )
                    }
                    navController.navigate(destination)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onViewHistory = { navController.navigate(Screen.FarmHistory.route) },
                onEditFarm = { navController.navigate(Screen.FarmSetup.route) }
            )
        }

        composable(Screen.FarmHistory.route) {
            FarmHistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
