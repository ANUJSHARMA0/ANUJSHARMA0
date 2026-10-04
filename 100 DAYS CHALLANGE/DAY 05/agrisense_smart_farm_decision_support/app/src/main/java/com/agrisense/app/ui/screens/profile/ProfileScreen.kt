package com.agrisense.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
fun ProfileScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onViewHistory: () -> Unit,
    onEditFarm: () -> Unit
) {
    val farm by viewModel.farm.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            // Profile card
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PrimaryContainer), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(shape = CircleShape, color = OnPrimary, modifier = Modifier.size(80.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text("👤", style = MaterialTheme.typography.displaySmall) }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(farm.farmerName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = OnPrimary)
                    Text("Kisan Parivar • ${farm.location}", style = MaterialTheme.typography.bodySmall, color = PrimaryFixedDim)
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Farm Details", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                    Spacer(Modifier.height(8.dp))
                    ProfileRow("Plot", farm.plotName)
                    ProfileRow("Area", "${farm.areaAcres} acres")
                    ProfileRow("Soil Type", farm.soilType.displayName)
                    ProfileRow("Irrigation", farm.irrigationType)
                    ProfileRow("Water Access", farm.waterAvailability.displayName)
                    ProfileRow("Farming Goal", farm.farmingGoal.displayName)
                    ProfileRow("Previous Crop", farm.previousCrop)
                    ProfileRow("District", farm.district)
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                onClick = onEditFarm,
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Farm Setup", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                        Text("Update farm details and preferences", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("Edit", style = MaterialTheme.typography.labelMedium, color = Secondary)
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                onClick = onViewHistory,
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Farm History", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                        Text("${viewModel.farmHistory.size} demo seasons with yield and profit summaries", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View farm history", tint = Secondary)
                }
            }

            Spacer(Modifier.height(12.dp))

            Surface(shape = RoundedCornerShape(12.dp), color = SurfaceContainerHigh, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp)) { Text("ℹ️", style = MaterialTheme.typography.labelMedium); Spacer(Modifier.width(8.dp)); Text("Profile data is demo/prototype only. Real app would support user auth and cloud sync.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Primary)
    }
}
