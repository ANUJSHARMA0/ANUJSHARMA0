package com.agrisense.app.ui.screens.weather

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
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val weather by viewModel.weather.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Weather Analysis", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("7-Day Forecast • Demo Data", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            // Current conditions card
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CURRENT CONDITIONS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("${weather.currentTempC.toInt()}°C", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Primary)
                            Text("Feels like ${weather.feelsLikeC.toInt()}°C", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("☁️", style = MaterialTheme.typography.displaySmall)
                            Text(weather.condition, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        WeatherStat("💧", "Humidity", "${weather.humidity}%")
                        WeatherStat("💨", "Wind", "${weather.windKmh.toInt()} km/h ${weather.windDirection}")
                        WeatherStat("☀️", "UV Index", "${weather.uvIndex}")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Rainfall forecast
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Rainfall Forecast", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                        Text("Modeled", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("${weather.rainfallForecastMm.toInt()} mm expected this week", style = MaterialTheme.typography.titleMedium, color = Primary)
                    Spacer(Modifier.height(4.dp))
                    Text(weather.rainfallBaseline, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (weather.hasDeficitAlert) {
                        Spacer(Modifier.height(8.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = ErrorContainer) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🔴", style = MaterialTheme.typography.labelSmall)
                                Spacer(Modifier.width(8.dp))
                                Text("Deficit alert — water-intensive crops may face stress", style = MaterialTheme.typography.bodySmall, color = OnErrorContainer)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 7-day forecast
            Text("7-Day Outlook", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
            Text("Estimated daily conditions", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))

            weather.dailyForecasts.forEach { day ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.width(60.dp)) {
                            Text(day.day, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                            Text(day.dateLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            when { "Rain" in day.condition -> "🌧️"; "Cloud" in day.condition -> "☁️"; else -> "☀️" },
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(day.condition, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("${day.tempHighC.toInt()}° / ${day.tempLowC.toInt()}°", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (day.rainfallMm > 0) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${day.rainfallMm.toInt()} mm", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary)
                                LinearProgressIndicator(progress = { (day.rainfallMm / 15.0).toFloat().coerceIn(0f, 1f) }, modifier = Modifier.width(40.dp).height(3.dp).clip(RoundedCornerShape(2.dp)), color = Secondary, trackColor = SurfaceContainer)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Crop weather impact
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SecondaryContainer.copy(alpha = 0.2f)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🌾 Crop Weather Impact", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                    Spacer(Modifier.height(8.dp))
                    Text("• Current 29°C temperature favors short-duration Kharif crops (Maize, Mustard).\n• Below-average rainfall forecast suggests prioritizing low-water crops.\n• High humidity (72%) increases pest surveillance need for fungal diseases.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun WeatherStat(icon: String, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, style = MaterialTheme.typography.titleMedium)
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
