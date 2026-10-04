package com.agrisense.app.ui.screens.scenario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.agrisense.app.domain.model.WhatIfParams
import com.agrisense.app.domain.model.WaterLevel
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioResultsScreen(viewModel: MainViewModel, onBack: () -> Unit, onViewPlan: () -> Unit) {
    val cropId by viewModel.simulationCropId.collectAsState()
    val crop = requireNotNull(viewModel.getCropById(cropId))

    // Generate 3 scenario results
    val scenarios = listOf(
        Triple("Favorable", WhatIfParams(10, 0.0, 15, -5, WaterLevel.HIGH), "☀️"),
        Triple("Normal", WhatIfParams(0, 0.0, 0, 0, WaterLevel.MEDIUM), "⚖️"),
        Triple("Unfavorable", WhatIfParams(-25, 2.0, -10, 15, WaterLevel.LOW), "⛈️")
    ).map { (name, params, icon) ->
        Triple(name, viewModel.simulateForCrop(cropId, params), icon)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Scenario Analysis", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("${crop.name} • three modeled scenarios", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Surface(shape = RoundedCornerShape(12.dp), color = SurfaceContainerHigh, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Text("ℹ️", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(8.dp))
                    Text("Illustrative projections from demo inputs, including your selected crop and farm size. They are not live forecasts.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(Modifier.height(16.dp))

            scenarios.forEach { (name, result, icon) ->
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(icon, style = MaterialTheme.typography.headlineSmall)
                                Spacer(Modifier.width(8.dp))
                                Text("$name Scenario", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                            }
                            Surface(shape = RoundedCornerShape(20.dp), color = when (result.riskLevel) { RiskLevel.HIGH -> ErrorContainer; RiskLevel.MEDIUM -> TertiaryFixed; else -> SecondaryContainer }) {
                                Text(result.riskLevel.displayName, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = when (result.riskLevel) { RiskLevel.HIGH -> Error; RiskLevel.MEDIUM -> OnTertiaryFixed; else -> Secondary })
                            }
                        }
                        Spacer(Modifier.height(12.dp))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Estimated Profit", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("₹${formatIndianNumber(result.estimatedProfit)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Primary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Yield", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${result.estimatedYieldTHa} t/ha", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("vs Base:", style = MaterialTheme.typography.labelSmall, color = Outline)
                            Text("${if (result.profitChangeVsBasePct >= 0) "+" else ""}${result.profitChangeVsBasePct}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = if (result.profitChangeVsBasePct >= 0) Secondary else Error)
                        }

                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(progress = { result.waterStressPct / 100f }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)), color = if (result.waterStressPct > 60) Error else Secondary, trackColor = SurfaceContainer)
                        Text("Water stress: ${result.waterStressLevel}", style = MaterialTheme.typography.labelSmall, color = Outline)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Summary card
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SecondaryContainer.copy(alpha = 0.2f)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💡 Key Insight", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                    Spacer(Modifier.height(8.dp))
                    val bestCase = scenarios.maxByOrNull { it.second.estimatedProfit }
                    val worstCase = scenarios.minByOrNull { it.second.estimatedProfit }
                    Text("The profit spread between ${bestCase?.first ?: ""} (₹${formatIndianNumber(bestCase?.second?.estimatedProfit ?: 0)}) and ${worstCase?.first ?: ""} (₹${formatIndianNumber(worstCase?.second?.estimatedProfit ?: 0)}) scenarios is ₹${formatIndianNumber((bestCase?.second?.estimatedProfit ?: 0) - (worstCase?.second?.estimatedProfit ?: 0))}. Consider drought insurance or crop diversification to hedge downside risk.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = onViewPlan, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Text("Build Long-Term Crop Plan →", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
