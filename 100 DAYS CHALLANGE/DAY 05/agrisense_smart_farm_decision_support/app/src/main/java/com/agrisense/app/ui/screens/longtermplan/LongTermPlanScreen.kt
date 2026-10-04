package com.agrisense.app.ui.screens.longtermplan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agrisense.app.domain.engine.formatIndianNumber
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongTermPlanScreen(viewModel: MainViewModel, onBack: () -> Unit, onViewReport: () -> Unit) {
    val soil by viewModel.soil.collectAsState()
    val plan = viewModel.generateLongTermPlan()
    val cumulativeProfit = plan.sumOf { it.expectedProfit }
    val totalWater = plan.sumOf { it.waterUsageMm }
    var cumulativeN = soil.nitrogenKgHa.toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("3-Year Crop Plan", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Long-Term Rotation Strategy • Modeled", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Surface(shape = RoundedCornerShape(12.dp), color = SurfaceContainerHigh, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Text("ℹ️", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(8.dp))
                    Text("Modeled rotation plan. Actual outcomes depend on real weather, market prices, and farming practices.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Summary stats
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PrimaryContainer), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("3-YEAR SUMMARY", style = MaterialTheme.typography.labelSmall, color = PrimaryFixedDim, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("₹${formatIndianNumber(cumulativeProfit)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = OnPrimary)
                            Text("Cumulative Profit", style = MaterialTheme.typography.labelSmall, color = PrimaryFixedDim)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${totalWater}mm", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = OnPrimary)
                            Text("Total Water", style = MaterialTheme.typography.labelSmall, color = PrimaryFixedDim)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val netN = plan.sumOf { it.soilNitrogenChange }
                            Text("${if (netN > 0) "+" else ""}$netN kg", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = OnPrimary)
                            Text("Net N Change", style = MaterialTheme.typography.labelSmall, color = PrimaryFixedDim)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Year-by-year plan
            plan.forEachIndexed { index, yearPlan ->
                cumulativeN += yearPlan.soilNitrogenChange
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(8.dp), color = if (index == 0) SecondaryContainer else SurfaceContainerHigh, modifier = Modifier.size(36.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text("Y${yearPlan.year}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary) }
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Year ${yearPlan.year} — ${yearPlan.season.displayName}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                                    Text("${yearPlan.crop.emoji} ${yearPlan.crop.name} (${yearPlan.crop.variety})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StatItem("💰 Profit", "₹${formatIndianNumber(yearPlan.expectedProfit)}")
                            StatItem("💧 Water", "${yearPlan.waterUsageMm}mm")
                            StatItem("🌿 N Impact", "${if (yearPlan.soilNitrogenChange > 0) "+" else ""}${yearPlan.soilNitrogenChange} kg")
                        }

                        Spacer(Modifier.height(8.dp))

                        // Soil N projection
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Projected Soil N after Year ${yearPlan.year}:", style = MaterialTheme.typography.labelSmall, color = Outline)
                            Text("$cumulativeN kg/ha", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = if (cumulativeN > 200) Secondary else AgriTerracotta)
                        }

                        Spacer(Modifier.height(8.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = SurfaceContainerLow, modifier = Modifier.fillMaxWidth()) {
                            Text("📝 ${yearPlan.rationale}", modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Soil health trajectory
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SecondaryContainer.copy(alpha = 0.2f)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🌱 Soil Health Trajectory", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                    Spacer(Modifier.height(8.dp))
                    Text("Starting nitrogen: ${soil.nitrogenKgHa.toInt()} kg/ha (${soil.nitrogenLevel.displayName})\nProjected after 3 years: $cumulativeN kg/ha", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(progress = { (cumulativeN.toFloat() / 300f).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = Secondary, trackColor = SurfaceContainer)
                    Spacer(Modifier.height(4.dp))
                    Text(if (cumulativeN > 200) "✅ Nitrogen levels improving — rotation strategy effective" else "⚠️ Additional soil amendment recommended", style = MaterialTheme.typography.labelSmall, color = if (cumulativeN > 200) Secondary else AgriTerracotta)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = onViewReport, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Text("Generate Farm Report →", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
