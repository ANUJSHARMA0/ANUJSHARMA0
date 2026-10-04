package com.agrisense.app.ui.screens.alerts

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
import com.agrisense.app.domain.model.AlertItem
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onAction: (AlertItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Alerts & Advisories", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("${viewModel.alerts.size} notifications • Demo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            viewModel.alerts.forEach { alert ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text(when (alert.severity) { "Urgent" -> "🔴"; "Climate" -> "🌧️"; "Favorable" -> "✅"; else -> "ℹ️" }, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(alert.title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                                Text(alert.timestamp, style = MaterialTheme.typography.labelSmall, color = Outline)
                            }
                            Spacer(Modifier.height(4.dp))
                            Surface(shape = RoundedCornerShape(10.dp), color = when (alert.severity) { "Urgent" -> ErrorContainer; "Climate" -> TertiaryFixed; "Favorable" -> SecondaryContainer; else -> SurfaceContainerHigh }) {
                                Text(alert.severity, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = when (alert.severity) { "Urgent" -> Error; "Climate" -> OnTertiaryFixed; "Favorable" -> Secondary; else -> MaterialTheme.colorScheme.onSurface })
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(alert.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            TextButton(onClick = { onAction(alert) }, contentPadding = PaddingValues(0.dp)) { Text(alert.actionLabel, style = MaterialTheme.typography.labelSmall, color = Secondary) }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
