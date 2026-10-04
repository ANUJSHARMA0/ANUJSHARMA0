package com.agrisense.app.ui.screens.market

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
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketInsightsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Market Insights", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Local Mandi Rates • Demo Data", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Surface(shape = RoundedCornerShape(12.dp), color = SurfaceContainerHigh, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp)) { Text("ℹ️", style = MaterialTheme.typography.labelMedium); Spacer(Modifier.width(8.dp)); Text("Demo mandi prices for prototype illustration. Real data would come from Agmarknet/eNAM API.", style = MaterialTheme.typography.bodySmall) }
            }
            Spacer(Modifier.height(16.dp))

            viewModel.marketPrices.forEach { price ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(price.cropName, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                            Text(price.mandiName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹${price.pricePerQuintal.toInt()}/q", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                            Text("${if (price.priceChange > 0) "↑ +" else "↓ "}₹${price.priceChange.toInt()}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (price.priceChange > 0) Secondary else Error)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
