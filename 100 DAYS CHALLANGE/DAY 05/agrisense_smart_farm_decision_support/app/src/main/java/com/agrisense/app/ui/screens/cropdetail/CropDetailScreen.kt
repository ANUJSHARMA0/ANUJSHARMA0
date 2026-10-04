package com.agrisense.app.ui.screens.cropdetail

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
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropDetailScreen(viewModel: MainViewModel, cropId: String, onBack: () -> Unit, onCompare: () -> Unit, onWhatIf: () -> Unit) {
    val rec = viewModel.getRecommendationForCrop(cropId)
    val crop = rec?.crop ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("${crop.emoji} ${crop.name}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Deep Analysis • ${crop.variety}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {

            // Suitability score card
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Suitability Score", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${rec.suitabilityScore}/100", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Primary)
                        }
                        Surface(shape = RoundedCornerShape(12.dp), color = if (rec.badge == "Top Match") SecondaryContainer else SurfaceContainerHigh) {
                            Text(rec.badge, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = if (rec.badge == "Top Match") OnSecondaryContainer else Primary)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(progress = { rec.suitabilityScore / 100f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = Secondary, trackColor = SurfaceContainer)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Score breakdown
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Score Breakdown", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                    Spacer(Modifier.height(12.dp))
                    BreakdownRow("🟫 Soil Match", rec.scoreBreakdown.soilScore, 25)
                    BreakdownRow("🌤️ Weather Fit", rec.scoreBreakdown.weatherScore, 17)
                    BreakdownRow("📅 Season Match", rec.scoreBreakdown.seasonScore, 5)
                    BreakdownRow("💧 Water Match", rec.scoreBreakdown.waterScore, 18)
                    BreakdownRow("💰 Economics", rec.scoreBreakdown.economicsScore, 18)
                    BreakdownRow("⏱️ Duration", rec.scoreBreakdown.durationScore, 10)
                    BreakdownRow("🎯 Farming Goal", rec.scoreBreakdown.goalScore, 7)
                    if (rec.scoreBreakdown.riskAdjustment < 0) {
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("⚠️ Risk Adjustment", style = MaterialTheme.typography.labelMedium, color = Error)
                            Text("${rec.scoreBreakdown.riskAdjustment}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Error)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Profit estimation
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💰 Estimated Profit Analysis", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                    Spacer(Modifier.height(12.dp))
                    Text("Expected Net Profit Range", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${formatIndianNumber(rec.expectedProfitRange.first)} – ₹${formatIndianNumber(rec.expectedProfitRange.last)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Primary)
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                    DetailRow(
                        "Expected Revenue (${viewModel.farm.value.areaAcres} acres)",
                        "₹${formatIndianNumber(rec.financialEstimate.expectedRevenueRange.first)}–₹${formatIndianNumber(rec.financialEstimate.expectedRevenueRange.last)}"
                    )
                    DetailRow(
                        "Estimated Total Cultivation Cost",
                        "₹${formatIndianNumber(rec.financialEstimate.cultivationCostRange.first)}–₹${formatIndianNumber(rec.financialEstimate.cultivationCostRange.last)}"
                    )
                    DetailRow("Yield Estimate (per farm)", "${"%.2f".format(rec.financialEstimate.expectedYieldTonnesRange.start)}–${"%.2f".format(rec.financialEstimate.expectedYieldTonnesRange.endInclusive)} t")
                    DetailRow("Yield Estimate (per hectare)", "${crop.yieldTonnesPerHa.start}–${crop.yieldTonnesPerHa.endInclusive} t/ha")
                    DetailRow("Cultivation Cost", "₹${formatIndianNumber((crop.costPerAcreRange.first + crop.costPerAcreRange.last) / 2)} /acre")
                    DetailRow("Duration", "${crop.durationDays.first}–${crop.durationDays.last} days")
                    DetailRow("Base Market Price", "₹${formatIndianNumber(crop.basePricePerQuintal)}/quintal")
                    DetailRow("Water Requirement", "${crop.waterMm} mm (${crop.waterRequirement.displayName})")
                    DetailRow("Soil N Impact", "${if (crop.soilNitrogenImpactKg > 0) "+" else ""}${crop.soilNitrogenImpactKg} kg/ha")
                    DetailRow("Model Confidence", "✓ ${rec.modelConfidence}%")
                    DetailRow("Risk Level", "${rec.riskLevel.emoji} ${rec.riskLevel.displayName}")
                }
            }

            Spacer(Modifier.height(12.dp))

            // Positive factors
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SecondaryContainer.copy(alpha = 0.2f)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("✅ Positive Factors", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Secondary)
                    Spacer(Modifier.height(8.dp))
                    rec.positiveFactors.forEach { factor ->
                        Text("• $factor", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 4.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Concerns
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ErrorContainer.copy(alpha = 0.3f)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚠️ Risk Factors", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Error)
                    Spacer(Modifier.height(8.dp))
                    rec.concerns.forEach { concern ->
                        Text("• $concern", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 4.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Action buttons
            Button(onClick = onWhatIf, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Text("Test in \"What If?\" Simulator →", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onCompare, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(24.dp)) {
                Text("🔀 Compare with Other Crops", style = MaterialTheme.typography.labelLarge, color = Primary)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BreakdownRow(label: String, score: Int, maxScore: Int) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Text("$score/$maxScore", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.width(48.dp))
        LinearProgressIndicator(progress = { score.toFloat() / maxScore }, modifier = Modifier.width(80.dp).height(6.dp).clip(RoundedCornerShape(3.dp)), color = Secondary, trackColor = SurfaceContainer)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary)
    }
}
