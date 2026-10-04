package com.agrisense.app.ui.screens.crops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.agrisense.app.domain.model.CropRecommendation
import com.agrisense.app.domain.model.RiskLevel
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropRecommendationsScreen(
    viewModel: MainViewModel,
    onCropDetail: (String) -> Unit,
    onCompare: () -> Unit,
    onBack: () -> Unit
) {
    val recommendations by viewModel.recommendations.collectAsState()
    val selectedIds by viewModel.selectedCropIds.collectAsState()
    val farm by viewModel.farm.collectAsState()
    val weather by viewModel.weather.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Crops for Your Farm", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Crop Recommendations", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                actions = { Surface(shape = CircleShape, color = SecondaryContainer) { Text("● ${farm.targetSeason.displayName} plan", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = OnSecondaryContainer) } ; Spacer(Modifier.width(8.dp)) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            // Subtitle
            Text("Modeled for ${farm.areaAcres} acres of ${farm.soilType.displayName.lowercase()} soil, ${farm.waterAvailability.displayName.lowercase()} water access, ${weather.rainfallForecastMm.toInt()}mm demo rainfall outlook, and your ${farm.targetSeason.displayName} ${farm.farmingGoal.displayName.lowercase()}.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Surface(shape = RoundedCornerShape(8.dp), color = SurfaceContainerHigh) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("ℹ️", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.width(8.dp))
                    Text("Illustrative estimates for decision support; actual results vary with field conditions.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Crop cards
            recommendations.forEach { rec ->
                CropRecommendationCard(
                    rec = rec,
                    isSelected = selectedIds.contains(rec.crop.id),
                    onToggleCompare = { viewModel.toggleCropSelection(rec.crop.id) },
                    onDetail = { onCropDetail(rec.crop.id) }
                )
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(8.dp))

            // Compare FAB
            if (selectedIds.size >= 2) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainer)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🔀", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text("${selectedIds.size} Crops Selected", style = MaterialTheme.typography.labelLarge, color = OnPrimary, fontWeight = FontWeight.Bold)
                            Text(selectedIds.joinToString(" vs ") { it.replaceFirstChar { c -> c.uppercase() } }, style = MaterialTheme.typography.labelSmall, color = PrimaryFixedDim)
                        }
                        Button(onClick = onCompare, shape = RoundedCornerShape(20.dp), colors = ButtonDefaults.buttonColors(containerColor = Secondary)) {
                            Text("Compare (${selectedIds.size}) →", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun CropRecommendationCard(rec: CropRecommendation, isSelected: Boolean, onToggleCompare: () -> Unit, onDetail: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(rec.crop.emoji, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("${rec.crop.name} (${rec.crop.variety})", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                        Surface(shape = RoundedCornerShape(10.dp), color = if (rec.badge == "Top Match") SecondaryContainer else SurfaceContainerHigh) {
                            Text(
                                "${if (rec.badge == "Top Match") "🏆" else if (rec.badge == "High Stability") "🛡️" else if (rec.badge == "Soil Rebuilding") "🌿" else "⚠️"} ${rec.badge}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall,
                                color = if (rec.badge == "Top Match") OnSecondaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                // Score circle
                Surface(shape = CircleShape, color = SurfaceContainer, modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${rec.suitabilityScore}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                            Text("/100", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Profit range
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Expected Net Profit (Estimated)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${formatIndianNumber(rec.expectedProfitRange.first)} –\n₹${formatIndianNumber(rec.expectedProfitRange.last)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                    Text(
                        "Revenue ₹${formatIndianNumber(rec.financialEstimate.expectedRevenueRange.first)}–₹${formatIndianNumber(rec.financialEstimate.expectedRevenueRange.last)} • Cost ₹${formatIndianNumber(rec.financialEstimate.cultivationCostRange.first)}–₹${formatIndianNumber(rec.financialEstimate.cultivationCostRange.last)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Model Confidence", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("✓ ${rec.modelConfidence}%", style = MaterialTheme.typography.labelLarge, color = Secondary)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Metrics row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MiniMetric("Modeled Risk", rec.riskLevel.displayName, when (rec.riskLevel) { RiskLevel.LOW, RiskLevel.LOW_MEDIUM -> Secondary; RiskLevel.MEDIUM -> AgriAmber; RiskLevel.HIGH -> Error })
                MiniMetric("Duration", "${rec.crop.durationDays.first}–${rec.crop.durationDays.last} d", MaterialTheme.colorScheme.onSurface)
                MiniMetric("Water Need", rec.crop.waterRequirement.displayName, if (rec.crop.waterRequirement == com.agrisense.app.domain.model.WaterLevel.HIGH) Error else Secondary)
            }

            Spacer(Modifier.height(8.dp))

            // Key insight
            Surface(shape = RoundedCornerShape(8.dp), color = SurfaceContainerLow, modifier = Modifier.fillMaxWidth()) {
                Text("✓ ${rec.positiveFactors.firstOrNull() ?: "Good fit for your farm"}", modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(Modifier.height(12.dp))

            // Action buttons
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onToggleCompare,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = if (isSelected) ButtonDefaults.outlinedButtonColors(containerColor = SecondaryContainer.copy(alpha = 0.3f)) else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(if (isSelected) "✓ Added to Compare" else "🔀 Add to Compare", style = MaterialTheme.typography.labelSmall, color = if (isSelected) Secondary else Primary)
                }
                Button(
                    onClick = onDetail,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
                ) {
                    Text("Deep Analysis →", style = MaterialTheme.typography.labelSmall, color = OnPrimary)
                }
            }
        }
    }
}

@Composable
private fun MiniMetric(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("● $value", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
