package com.agrisense.app.ui.screens.report

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agrisense.app.domain.engine.formatIndianNumber
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmReportScreen(viewModel: MainViewModel, onBack: () -> Unit, onBackToDashboard: () -> Unit) {
    val farm by viewModel.farm.collectAsState()
    val soil by viewModel.soil.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val recs by viewModel.recommendations.collectAsState()
    val plan = viewModel.generateLongTermPlan()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Farm Report", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("AgriSense Decision Summary", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {

            // Header
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PrimaryContainer), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🌾 AgriSense Farm Report", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = OnPrimary)
                    Text("Generated for ${farm.farmerName} • ${farm.location}", style = MaterialTheme.typography.bodySmall, color = PrimaryFixedDim)
                    Text("Report Type: Demonstration / Prototype", style = MaterialTheme.typography.labelSmall, color = PrimaryFixedDim)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Farm Profile
            ReportSection("🏡 Farm Profile") {
                ReportRow("Farmer", farm.farmerName)
                ReportRow("Location", farm.location)
                ReportRow("Area", "${farm.areaAcres} acres")
                ReportRow("Soil Type", farm.soilType.displayName)
                ReportRow("Irrigation", farm.irrigationType)
                ReportRow("Water Access", farm.waterAvailability.displayName)
                ReportRow("Previous Crop", farm.previousCrop)
                ReportRow("Farming Goal", farm.farmingGoal.displayName)
            }

            Spacer(Modifier.height(12.dp))

            // Soil Analysis
            ReportSection("⚗️ Soil Analysis (Demo Data)") {
                ReportRow("pH", "${soil.ph} (${soil.phStatus})")
                ReportRow("Nitrogen", "${soil.nitrogenKgHa.toInt()} kg/ha (${soil.nitrogenLevel.displayName})")
                ReportRow("Phosphorus", "${soil.phosphorusKgHa} kg/ha (${soil.phosphorusLevel.displayName})")
                ReportRow("Potassium", "${soil.potassiumKgHa.toInt()} kg/ha (${soil.potassiumLevel.displayName})")
                ReportRow("Organic Carbon", "${soil.organicCarbonPct}%")
                ReportRow("Soil Health Score", "${soil.soilHealthScore}/100")
            }

            Spacer(Modifier.height(12.dp))

            // Weather
            ReportSection("🌤️ Weather Outlook (Demo Data)") {
                ReportRow("Temperature", "${weather.currentTempC.toInt()}°C (${weather.condition})")
                ReportRow("Humidity", "${weather.humidity}%")
                ReportRow("7-Day Rainfall", "${weather.rainfallForecastMm.toInt()} mm")
                ReportRow("Status", if (weather.hasDeficitAlert) "⚠️ Deficit Alert" else "✅ Normal")
            }

            Spacer(Modifier.height(12.dp))

            // Crop Recommendations
            ReportSection("🌽 Top Crop Recommendations (Modeled)") {
                recs.take(4).forEachIndexed { i, rec ->
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (i == 0) SecondaryContainer.copy(alpha = 0.2f) else SurfaceContainerLow), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("${i + 1}.", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text("${rec.crop.emoji} ${rec.crop.name} (${rec.crop.variety})", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary)
                                Text("Score: ${rec.suitabilityScore}/100 • Confidence: ${rec.modelConfidence}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Yield: ${"%.1f".format(rec.financialEstimate.expectedYieldTonnesRange.start)}–${"%.1f".format(rec.financialEstimate.expectedYieldTonnesRange.endInclusive)} t", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Revenue ₹${formatIndianNumber(rec.financialEstimate.expectedRevenueRange.first)}–₹${formatIndianNumber(rec.financialEstimate.expectedRevenueRange.last)} • Cost ₹${formatIndianNumber(rec.financialEstimate.cultivationCostRange.first)}–₹${formatIndianNumber(rec.financialEstimate.cultivationCostRange.last)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹${formatIndianNumber(rec.expectedProfitRange.first)}–${formatIndianNumber(rec.expectedProfitRange.last)}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Primary)
                                Text(rec.riskLevel.displayName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // 3-Year Plan Summary
            if (plan.isNotEmpty()) {
                ReportSection("📅 3-Year Crop Rotation Plan (Modeled)") {
                    plan.forEach { yearPlan ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Year ${yearPlan.year}: ${yearPlan.crop.emoji} ${yearPlan.crop.name}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary)
                            Text("₹${formatIndianNumber(yearPlan.expectedProfit)}", style = MaterialTheme.typography.labelMedium, color = Secondary)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("3-Year Cumulative:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                        Text("₹${formatIndianNumber(plan.sumOf { it.expectedProfit })}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Secondary)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Disclaimer
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚠️ Important Disclaimer", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                    Spacer(Modifier.height(4.dp))
                    Text("This report contains illustrative estimates generated by the AgriSense prototype for educational and demonstration purposes. Profit projections, suitability scores, and recommendations use local demo data and simplified models; actual results can vary. Consult local KVK agronomists and ICAR guidelines before making farming decisions.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = onBackToDashboard, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Text("Back to Dashboard", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ReportSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun ReportRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary)
    }
}
