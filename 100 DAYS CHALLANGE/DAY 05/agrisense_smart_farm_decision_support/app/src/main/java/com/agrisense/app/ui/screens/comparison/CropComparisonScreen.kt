package com.agrisense.app.ui.screens.comparison

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agrisense.app.domain.engine.formatIndianNumber
import com.agrisense.app.domain.model.RiskLevel
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropComparisonScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onWhatIf: () -> Unit,
    onSelectForPlan: () -> Unit
) {
    val selected = viewModel.getSelectedRecommendations()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Crop Comparison", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Side-by-side trade-off evaluation", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            // Info banner
            Surface(shape = RoundedCornerShape(12.dp), color = SurfaceContainerHigh, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Text("ℹ️", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(8.dp))
                    Text("No single crop is universally \"best\". Evaluate profit potential versus groundwater drawdown and upfront capital expense.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Suitability Scores
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📊", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.width(8.dp))
                        Text("Suitability Score", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Modeled by AgriSense Engine", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))

                    selected.forEach { rec ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("${rec.crop.emoji} ${rec.crop.name}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.width(100.dp))
                            Text("${rec.suitabilityScore}/100", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Secondary, modifier = Modifier.width(50.dp))
                            LinearProgressIndicator(
                                progress = { rec.suitabilityScore / 100f },
                                modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp)),
                                color = Secondary,
                                trackColor = SurfaceContainer
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Side-by-Side Trade-offs table
            Text("Side-by-Side Trade-offs", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
            Text("Modeled estimates per farm basis", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                Column {
                    // Header row
                    Surface(color = SurfaceContainerLow, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Text("Parameter", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                            selected.forEach { rec ->
                                Text(rec.crop.name.take(8), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Primary, textAlign = TextAlign.Center, modifier = Modifier.width(72.dp))
                            }
                        }
                    }

                    // Data rows
                    ComparisonRow("💰 Net Profit", selected.map { "₹${formatIndianNumber(it.expectedProfitRange.first)}–${formatIndianNumber(it.expectedProfitRange.last)}" }, false)
                    ComparisonRow("📈 Expected Revenue", selected.map { "₹${formatIndianNumber(it.financialEstimate.expectedRevenueRange.first)}–${formatIndianNumber(it.financialEstimate.expectedRevenueRange.last)}" }, true)
                    ComparisonRow("🧾 Cultivation Cost", selected.map { "₹${formatIndianNumber(it.financialEstimate.cultivationCostRange.first)}–${formatIndianNumber(it.financialEstimate.cultivationCostRange.last)}" }, false)
                    ComparisonRow("🌾 Expected Yield", selected.map { "${"%.1f".format(it.financialEstimate.expectedYieldTonnesRange.start)}–${"%.1f".format(it.financialEstimate.expectedYieldTonnesRange.endInclusive)} t" }, true)
                    ComparisonRow("🛡️ Modeled Risk", selected.map { "${it.riskLevel.emoji} ${it.riskLevel.displayName}" }, true)
                    ComparisonRow("⏱️ Duration", selected.map { "${it.crop.durationDays.first}–${it.crop.durationDays.last}d" }, false)
                    ComparisonRow("💧 Water Need", selected.map { "${it.crop.waterMm}mm\n${it.crop.waterRequirement.displayName}" }, true)
                    ComparisonRow("💵 Cost/Acre", selected.map { "₹${formatIndianNumber((it.crop.costPerAcreRange.first + it.crop.costPerAcreRange.last) / 2)}" }, false)
                    ComparisonRow("🌿 Soil N Impact", selected.map { val n = it.crop.soilNitrogenImpactKg; "${if (n > 0) "+$n" else "$n"} kg" }, true)
                    ComparisonRow("✓ Confidence", selected.map { "${it.modelConfidence}%" }, false)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Trade-off takeaway
            Text("💡 Trade-Off Takeaway", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(Modifier.height(8.dp))

            val highestProfit = selected.maxByOrNull { it.expectedProfitRange.last }
            val lowestWater = selected.minByOrNull { it.crop.waterMm }
            val soilHealer = selected.maxByOrNull { it.crop.soilNitrogenImpactKg }

            if (highestProfit != null) TakeawayCard("📈", "Highest Profit Potential", highestProfit.crop.name, "${highestProfit.crop.name} delivers maximum upside if monsoon rainfall is normal and market demand stays strong.")
            Spacer(Modifier.height(8.dp))
            if (lowestWater != null) TakeawayCard("☁️", "Safest Climate Bet", lowestWater.crop.name, "${lowestWater.crop.name} offers lower downside risk under dry spells with minimal irrigation.")
            Spacer(Modifier.height(8.dp))
            if (soilHealer != null && soilHealer.crop.soilNitrogenImpactKg > 0) TakeawayCard("🌿", "Best for Soil Rebuilding", soilHealer.crop.name, "${soilHealer.crop.name} restores nitrogen levels (+${soilHealer.crop.soilNitrogenImpactKg} kg N/ha), lowering fertilizer bills.")

            Spacer(Modifier.height(20.dp))

            // Action buttons
            Button(onClick = onWhatIf, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Text("Test in \"What If?\" Simulator →", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onSelectForPlan, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(24.dp)) {
                Text("📅 Select Crop for 3-Year Plan", style = MaterialTheme.typography.labelLarge, color = Primary)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ComparisonRow(label: String, values: List<String>, shaded: Boolean) {
    Surface(color = if (shaded) SurfaceContainerLow else SurfaceContainerLowest, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            values.forEach { value ->
                Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Primary, textAlign = TextAlign.Center, modifier = Modifier.width(72.dp))
            }
        }
    }
}

@Composable
private fun TakeawayCard(icon: String, title: String, cropName: String, description: String) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp)) {
            Surface(shape = RoundedCornerShape(20.dp), color = SecondaryContainer, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(icon, style = MaterialTheme.typography.titleSmall) }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(title, style = MaterialTheme.typography.labelLarge, color = Primary)
                    Surface(shape = RoundedCornerShape(10.dp), color = SurfaceContainer) {
                        Text(cropName, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Primary)
                    }
                }
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
