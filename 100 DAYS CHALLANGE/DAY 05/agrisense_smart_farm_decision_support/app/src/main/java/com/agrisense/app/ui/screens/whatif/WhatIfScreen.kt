package com.agrisense.app.ui.screens.whatif

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agrisense.app.domain.engine.formatIndianNumber
import com.agrisense.app.domain.model.RiskLevel
import com.agrisense.app.domain.model.WaterLevel
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatIfScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onViewScenario: () -> Unit,
    onViewPlan: () -> Unit
) {
    val params by viewModel.whatIfParams.collectAsState()
    val result by viewModel.whatIfResult.collectAsState()
    val selectedCropId by viewModel.simulationCropId.collectAsState()
    val crop = requireNotNull(viewModel.getCropById(selectedCropId))
    val sim = viewModel.simulator

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("What If? Simulator", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Transparent demonstration model", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Text("Change the assumptions and compare modeled yield, profit, and risk. These are local demo calculations, not live forecasts.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = SurfaceContainerHigh, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Model: yield changes with rainfall, temperature relative to each crop's preferred range, and crop-specific water access. Estimated profit = modeled yield × adjusted demo price − estimated cultivation and irrigation costs.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(12.dp))

            Text("Crop to simulate", style = MaterialTheme.typography.labelLarge, color = Primary)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.crops.forEach { option ->
                    FilterChip(
                        selected = option.id == selectedCropId,
                        onClick = { viewModel.selectSimulationCrop(option.id) },
                        label = { Text("${option.emoji} ${option.name}") }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            // Modeled demo outcome
            result?.let { res ->
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(10.dp).clip(CircleShape).padding(0.dp)) { Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = Secondary) {} }
                                Spacer(Modifier.width(8.dp))
                                Text("${crop.name} • demo estimate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(shape = RoundedCornerShape(20.dp), color = when (res.riskLevel) { RiskLevel.HIGH -> TertiaryFixed; RiskLevel.MEDIUM -> TertiaryFixed; else -> SecondaryContainer }) {
                                Text("⚠️ ${res.riskLevel.displayName} Risk", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = when (res.riskLevel) { RiskLevel.HIGH -> OnTertiaryFixed; RiskLevel.MEDIUM -> OnTertiaryFixed; else -> OnSecondaryContainer })
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Estimated Net Profit", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("₹${formatIndianNumber(res.estimatedProfit)}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Primary)
                                Surface(shape = RoundedCornerShape(20.dp), color = if (res.profitChangeVsBasePct < 0) ErrorContainer else SecondaryContainer) {
                                    Text("${if (res.profitChangeVsBasePct >= 0) "+" else ""}${res.profitChangeVsBasePct}% vs Base", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (res.profitChangeVsBasePct < 0) Error else Secondary)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Modeled Yield", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${res.estimatedYieldTHa} t/ha", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Primary)
                                Text("Base ${res.baseYield} t/ha", style = MaterialTheme.typography.labelSmall, color = Outline)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Water stress bar
                        Surface(shape = RoundedCornerShape(8.dp), color = SurfaceContainerLow, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("💧", style = MaterialTheme.typography.labelMedium)
                                        Spacer(Modifier.width(4.dp))
                                        Text("Water Stress Impact", style = MaterialTheme.typography.labelMedium)
                                    }
                                    Text(res.waterStressLevel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (res.waterStressPct > 60) Error else Secondary)
                                }
                                Spacer(Modifier.height(4.dp))
                                LinearProgressIndicator(progress = { res.waterStressPct / 100f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = if (res.waterStressPct > 60) Error else Secondary, trackColor = SurfaceContainer)
                                Spacer(Modifier.height(4.dp))
                                Text("ℹ️ ${res.waterStressDescription}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Simulation Variables
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚙️", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.width(8.dp))
                            Text("Simulation Variables", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                        }
                        TextButton(onClick = { viewModel.resetSimulation() }) { Text("↺ Reset", style = MaterialTheme.typography.labelSmall, color = Secondary) }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Rainfall slider
                    SimSlider("🌧️ Rainfall Anomaly", sim.formatRainfallLabel(params.rainfallPct), params.rainfallPct.toFloat(), -30f, 30f, listOf("-30% Deficit", "Baseline", "+30% Surplus")) { viewModel.updateRainfall(it.toInt()) }
                    Spacer(Modifier.height(16.dp))

                    // Temperature slider
                    SimSlider("🌡️ Temperature Shift", sim.formatTemperatureLabel(params.temperatureShiftC), params.temperatureShiftC.toFloat(), -3f, 3f, listOf("-3.0°C", "Normal", "+3.0°C Heatwave")) { viewModel.updateTemperature(it.toDouble()) }
                    Spacer(Modifier.height(16.dp))

                    // Market price slider
                    SimSlider("₹ Mandi Market Price", sim.formatPriceLabel(params.marketPricePct, crop), params.marketPricePct.toFloat(), -20f, 20f, listOf("-20% Change", "₹${crop.basePricePerQuintal}/q", "+20% Change")) { viewModel.updateMarketPrice(it.toInt()) }
                    Spacer(Modifier.height(16.dp))

                    // Cost slider
                    SimSlider("⛽ Fertilizer & Diesel", sim.formatCostLabel(params.fertilizerCostPct), params.fertilizerCostPct.toFloat(), -20f, 30f, listOf("-20% Subsidized", "Base", "+30% Spike")) { viewModel.updateFertilizerCost(it.toInt()) }
                    Spacer(Modifier.height(16.dp))

                    // Water slider
                    Text("🚿 Irrigation Water Access", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(4.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = SurfaceContainerHigh) {
                        Text(sim.formatWaterLabel(params.waterAvailability), modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    val waterVal = when (params.waterAvailability) { WaterLevel.LOW -> 1f; WaterLevel.MEDIUM -> 2f; WaterLevel.HIGH -> 3f }
                    Slider(value = waterVal, onValueChange = { v -> viewModel.updateWaterAvailability(when { v <= 1.5f -> WaterLevel.LOW; v <= 2.5f -> WaterLevel.MEDIUM; else -> WaterLevel.HIGH }) }, valueRange = 1f..3f, steps = 1)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Low Canal", style = MaterialTheme.typography.labelSmall, color = Outline)
                        Text("Medium Canal", style = MaterialTheme.typography.labelSmall, color = Outline)
                        Text("High Tube Well", style = MaterialTheme.typography.labelSmall, color = Outline)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Preconfigured Stress Tests
            Text("Preconfigured Stress Tests", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
            Text("Tap to apply", style = MaterialTheme.typography.labelSmall, color = Outline)
            Spacer(Modifier.height(8.dp))

            viewModel.scenarioPresets.forEach { preset ->
                val presetResult = viewModel.simulateForCrop(selectedCropId, preset.params)
                Card(
                    onClick = { viewModel.applyPreset(preset) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(8.dp), color = when (presetResult.riskLevel) { RiskLevel.HIGH -> TertiaryFixed; RiskLevel.LOW, RiskLevel.LOW_MEDIUM -> SecondaryFixed; else -> SurfaceContainerHigh }, modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) { Text(when (preset.icon) { "sunny" -> "☀️"; "trending_up" -> "📈"; else -> "⚖️" }, style = MaterialTheme.typography.titleSmall) }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(preset.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(preset.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹${formatIndianNumber(presetResult.estimatedProfit)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary)
                            Text(presetResult.riskLevel.displayName, style = MaterialTheme.typography.labelSmall, color = when (presetResult.riskLevel) { RiskLevel.HIGH -> Error; else -> Secondary })
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // CTA buttons
            Button(onClick = onViewPlan, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)) {
                Text("View Multi-Year Crop Plan →", style = MaterialTheme.typography.labelLarge, color = OnPrimary)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onViewScenario, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(24.dp)) {
                Text("📊 View Scenario Analysis", style = MaterialTheme.typography.labelLarge, color = Primary)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SimSlider(label: String, valueLabel: String, value: Float, min: Float, max: Float, ticks: List<String>, onChange: (Float) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        Surface(shape = RoundedCornerShape(20.dp), color = SurfaceContainerHigh) {
            Text(valueLabel, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
    Slider(value = value, onValueChange = onChange, valueRange = min..max)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        ticks.forEach { Text(it, style = MaterialTheme.typography.labelSmall, color = Outline) }
    }
}
