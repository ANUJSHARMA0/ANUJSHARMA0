package com.agrisense.app.ui.screens.soil

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
import com.agrisense.app.domain.model.NutrientLevel
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilAnalysisScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val soil by viewModel.soil.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Soil Analysis", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Detailed NPK Profile", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            // Demo data banner
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = SurfaceContainerHigh) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("ℹ️", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.width(8.dp))
                    Text("Demo Data — Values represent a typical alluvial loam soil profile from western UP region.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(Modifier.height(16.dp))

            // pH Card
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SOIL pH", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${soil.ph}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Primary)
                        Spacer(Modifier.width(8.dp))
                        Text("pH (${soil.phStatus})", style = MaterialTheme.typography.labelLarge, color = Secondary)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(progress = { (soil.ph / 14.0).toFloat() }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = Secondary, trackColor = SurfaceContainer)
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Acidic (0)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Neutral (7)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Alkaline (14)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // NPK Cards
            listOf(
                Triple("Nitrogen (N)", soil.nitrogenKgHa, soil.nitrogenLevel),
                Triple("Phosphorus (P)", soil.phosphorusKgHa, soil.phosphorusLevel),
                Triple("Potassium (K)", soil.potassiumKgHa, soil.potassiumLevel)
            ).forEach { (name, value, level) ->
                NutrientCard(name, value, "kg/ha", level)
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(12.dp))

            // Additional metrics
            Text("Additional Metrics", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(Modifier.height(8.dp))

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MetricRow("Organic Carbon", "${soil.organicCarbonPct}%", if (soil.organicCarbonPct < 0.5) "Low" else "Adequate")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    MetricRow("EC (Salinity)", "${soil.ecDsM} dS/m", "Normal")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    MetricRow("Zinc", "${soil.zincPpm} ppm", "Deficient")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    MetricRow("Iron", "${soil.ironPpm} ppm", "Adequate")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    MetricRow("Last Tested", soil.lastTestedDate, "")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    MetricRow("Soil Health Score", "${soil.soilHealthScore}/100", "")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Recommendation
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SecondaryContainer.copy(alpha = 0.3f)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💡 Soil Advisory", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                    Spacer(Modifier.height(8.dp))
                    Text("• Nitrogen is critically low — consider green manuring with Dhaincha or Sesbania before sowing.\n• Organic carbon below 0.5% indicates need for FYM/compost application (5-8 t/ha).\n• Zinc deficiency may limit maize grain fill; consider ZnSO₄ foliar spray.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun NutrientCard(name: String, value: Double, unit: String, level: NutrientLevel) {
    val color = when (level) { NutrientLevel.LOW -> AgriTerracotta; NutrientLevel.MEDIUM -> AgriAmber; NutrientLevel.HIGH -> Secondary }
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.15f)) {
                    Text("${level.emoji} ${level.displayName}", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("${value.toInt()}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Primary)
                Spacer(Modifier.width(4.dp))
                Text(unit, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = { (value / 400.0).toFloat().coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)), color = color, trackColor = SurfaceContainer)
        }
    }
}

@Composable
fun MetricRow(label: String, value: String, status: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary)
            if (status.isNotEmpty()) { Spacer(Modifier.width(8.dp)); Text(status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}
