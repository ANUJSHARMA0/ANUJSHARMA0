package com.agrisense.app.ui.screens.sensors

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("IoT Sensors", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("ESP32 Field Nodes • Demo Data", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Surface(shape = RoundedCornerShape(12.dp), color = SurfaceContainerHigh, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp)) { Text("ℹ️", style = MaterialTheme.typography.labelMedium); Spacer(Modifier.width(8.dp)); Text("Demo sensor readings. Real deployment would use ESP32 nodes with MQTT/Firebase.", style = MaterialTheme.typography.bodySmall) }
            }
            Spacer(Modifier.height(16.dp))

            val activeCount = viewModel.sensorReadings.count { it.status == "Active" }
            val attentionCount = viewModel.sensorReadings.count { it.status != "Active" }
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Simulated device status", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                        Text("${viewModel.sensorReadings.size} demo nodes • No hardware connected", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("$activeCount online\n$attentionCount attention", style = MaterialTheme.typography.labelSmall, color = if (attentionCount > 0) AgriTerracotta else Secondary)
                }
            }

            viewModel.sensorReadings.forEach { sensor ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = when (sensor.status) { "Active" -> SecondaryContainer; "Warning" -> TertiaryFixed; else -> SurfaceContainerHigh }, modifier = Modifier.size(10.dp)) {}
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(sensor.sensorName, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                            Text("${sensor.location} • ${sensor.lastUpdated}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${formatReading(sensor.value)}${sensor.unit}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                            Text(sensor.status, style = MaterialTheme.typography.labelSmall, color = when (sensor.status) { "Active" -> Secondary; "Warning" -> AgriTerracotta; else -> Outline })
                        }
                    }

                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun formatReading(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else "%.1f".format(value)
