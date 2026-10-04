package com.agrisense.app.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agrisense.app.domain.model.NutrientLevel
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToSoil: () -> Unit,
    onNavigateToWeather: () -> Unit,
    onNavigateToCrops: () -> Unit,
    onNavigateToMarket: () -> Unit,
    onNavigateToSensors: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onEditFarm: () -> Unit
) {
    val farm by viewModel.farm.collectAsState()
    val soil by viewModel.soil.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌱", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("AgriSense", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                            Text("Home", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    Surface(shape = CircleShape, color = SurfaceContainer) {
                        Text("EN", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary)
                    }
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = onNavigateToAlerts) { Text("🔔") }
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Primary).clickable { onNavigateToProfile() }, contentAlignment = Alignment.Center) {
                        Text("👤", style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Greeting
            Text("KISAN PARIVAR", style = MaterialTheme.typography.labelSmall, color = Secondary, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Good morning, ${farm.farmerName}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(20.dp), color = SurfaceContainer) {
                    Text("☀️ ${weather.currentTempC.toInt()}°C", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Farm context card
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📍")
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${farm.areaAcres} acres • ${farm.location}", style = MaterialTheme.typography.labelMedium)
                        Text("${farm.plotName} (${farm.soilType.displayName})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = onEditFarm) { Text("Edit", style = MaterialTheme.typography.labelSmall, color = Secondary) }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Field Vitals
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Field Vitals", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(Secondary))
                    Spacer(Modifier.width(4.dp))
                    Text("Demo profile", style = MaterialTheme.typography.labelSmall, color = Secondary)
                }
            }
            Spacer(Modifier.height(12.dp))

            // Vitals grid
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                // Soil Health Card
                VitalCard(modifier = Modifier.weight(1f), title = "SOIL HEALTH", icon = "⚗️", onClick = onNavigateToSoil) {
                    Text("${soil.ph}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Primary)
                    Text("pH (${soil.phStatus})", style = MaterialTheme.typography.labelSmall, color = Secondary)
                    Spacer(Modifier.height(4.dp))
                    Text("⚠️ N: ${soil.nitrogenLevel.displayName} (${soil.nitrogenKgHa.toInt()} kg/ha)", style = MaterialTheme.typography.labelSmall, color = if (soil.nitrogenLevel == NutrientLevel.LOW) AgriTerracotta else MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("P: ${soil.phosphorusLevel.displayName}    K: ${soil.potassiumLevel.displayName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("Soil Card →", style = MaterialTheme.typography.labelSmall, color = Secondary)
                }

                // Weather Card
                VitalCard(modifier = Modifier.weight(1f), title = "WEATHER 7D", icon = "🌤️", onClick = onNavigateToWeather) {
                    Text("${weather.rainfallForecastMm.toInt()}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Primary)
                    Text("mm rain", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(weather.rainfallBaseline, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    if (weather.hasDeficitAlert) {
                        Text("🔴 Deficit alert", style = MaterialTheme.typography.labelSmall, color = Error)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                VitalCard(modifier = Modifier.weight(1f), title = "MOISTURE", icon = "💧", onClick = onNavigateToSensors) {
                    Text("42%", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Primary)
                    Text("Demo moisture", style = MaterialTheme.typography.labelSmall, color = Secondary)
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(progress = { 0.42f }, modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)), color = Secondary, trackColor = SurfaceContainer)
                    Spacer(Modifier.height(4.dp))
                    Text("Canal + Tubewell", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Demo sensor reading", style = MaterialTheme.typography.labelSmall, color = Secondary)
                }

                VitalCard(modifier = Modifier.weight(1f), title = "MANDI PULSE", icon = "📈", onClick = onNavigateToMarket) {
                    Text("Surajpur Mandi", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    viewModel.marketPrices.take(2).forEach { price ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(price.cropName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("₹${price.pricePerQuintal.toInt()} ${if (price.priceChange > 0) "↑" else "↓"}", style = MaterialTheme.typography.labelSmall, color = if (price.priceChange > 0) Secondary else Error)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Rates →", style = MaterialTheme.typography.labelSmall, color = Secondary)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Smart Crop Suitability
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SecondaryContainer.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(20.dp), color = SecondaryContainer) {
                            val confidenceRange = recommendations
                                .take(3)
                                .map { it.modelConfidence }
                                .takeIf { it.isNotEmpty() }
                                ?.let { "${it.minOrNull()}–${it.maxOrNull()}%" }
                                ?: "No estimates yet"
                            Text("✅ $confidenceRange demo confidence", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = OnSecondaryContainer)
                        }
                        Spacer(Modifier.weight(1f))
                        Text("${farm.targetSeason.displayName} Plan", style = MaterialTheme.typography.labelSmall, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Smart Crop Suitability Ready", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                    Text("Based on your ${farm.areaAcres} acre ${farm.soilType.displayName.lowercase()} soil profile and ${weather.rainfallForecastMm.toInt()}mm demo rainfall outlook, ${recommendations.size} crop candidates are modeled for your ${farm.targetSeason.displayName} plan.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        recommendations.take(3).forEach { rec ->
                            Surface(shape = RoundedCornerShape(20.dp), color = PrimaryContainer) {
                                Text("${rec.crop.emoji} ${rec.crop.name}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, color = OnPrimary)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onNavigateToCrops,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerLowest)
                    ) {
                        Text("View Crop Recommendations (${recommendations.size})", style = MaterialTheme.typography.labelLarge, color = Primary)
                        Spacer(Modifier.width(4.dp))
                        Text("→", color = Primary)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Action items
            Text("Your Farm Today", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(Modifier.width(8.dp))
            Text("${viewModel.alerts.count { it.severity == "Urgent" || it.severity == "Climate" }} Action Items", style = MaterialTheme.typography.labelSmall, color = Error)
            Spacer(Modifier.height(12.dp))

            viewModel.alerts.take(3).forEach { alert ->
                Card(
                    onClick = onNavigateToAlerts,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text(
                            when (alert.severity) { "Urgent" -> "⚠️"; "Climate" -> "🌧️"; "Favorable" -> "✅"; else -> "ℹ️" },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(alert.title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Text(alert.severity, style = MaterialTheme.typography.labelSmall, color = when (alert.severity) { "Urgent" -> Error; "Climate" -> Primary; "Favorable" -> Secondary; else -> MaterialTheme.colorScheme.onSurfaceVariant })
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(alert.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3)
                            Spacer(Modifier.height(4.dp))
                            Text(alert.actionLabel, style = MaterialTheme.typography.labelSmall, color = Secondary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun VitalCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: String,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                Text(icon, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}
