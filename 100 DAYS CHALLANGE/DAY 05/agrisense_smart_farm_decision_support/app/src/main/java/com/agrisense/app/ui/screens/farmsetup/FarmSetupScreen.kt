package com.agrisense.app.ui.screens.farmsetup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agrisense.app.domain.model.*
import com.agrisense.app.ui.theme.*
import com.agrisense.app.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmSetupScreen(
    viewModel: MainViewModel,
    onAnalyzeFarm: () -> Unit,
    onBack: () -> Unit
) {
    val farm by viewModel.farm.collectAsState()
    var selectedSoilType by remember { mutableStateOf(farm.soilType) }
    var selectedGoal by remember { mutableStateOf(farm.farmingGoal) }
    var selectedWaterAvailability by remember { mutableStateOf(farm.waterAvailability) }
    var selectedSeason by remember { mutableStateOf(farm.targetSeason) }
    var selectedIrrigationType by remember { mutableStateOf(farm.irrigationType) }
    var farmerNameText by remember { mutableStateOf(farm.farmerName) }
    var areaText by remember { mutableStateOf(farm.areaAcres.toString()) }
    var locationText by remember { mutableStateOf(farm.location) }
    var previousCropText by remember { mutableStateOf(farm.previousCrop) }
    val parsedArea = areaText.toDoubleOrNull()
    val areaIsValid = parsedArea?.let { it.isFinite() && it > 0.0 } == true
    val locationIsValid = locationText.isNotBlank()
    val farmerNameIsValid = farmerNameText.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Farm Setup", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("AgriSense • Configure Your Farm", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Progress indicator
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainerLow
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📊", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Step 1: Tell us about your farm", style = MaterialTheme.typography.labelLarge, color = Primary)
                        Text("This data helps generate tailored crop recommendations", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Farmer Name", style = MaterialTheme.typography.labelLarge, color = Primary)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = farmerNameText,
                onValueChange = { farmerNameText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Your name") },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                isError = farmerNameText.isNotEmpty() && !farmerNameIsValid,
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Farm Location
            Text("Farm Location", style = MaterialTheme.typography.labelLarge, color = Primary)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = locationText,
                onValueChange = { locationText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Location") },
                leadingIcon = { Text("📍") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Farm Area
            Text("Farm Area", style = MaterialTheme.typography.labelLarge, color = Primary)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = areaText,
                onValueChange = { areaText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Area in Acres") },
                leadingIcon = { Text("📐") },
                suffix = { Text("acres") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = areaText.isNotEmpty() && !areaIsValid,
                supportingText = {
                    if (areaText.isNotEmpty() && !areaIsValid) Text("Enter a finite farm area greater than zero.")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Soil Type Selection
            Text("Soil Type", style = MaterialTheme.typography.labelLarge, color = Primary)
            Spacer(Modifier.height(4.dp))
            Text("Select your primary soil type", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))

            val soilEmoji = mapOf(SoilType.LOAMY to "🟫", SoilType.CLAY to "🔵", SoilType.SANDY to "🟡", SoilType.ALLUVIAL to "🟤", SoilType.RED to "🔴", SoilType.BLACK_COTTON to "⚫")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SoilType.entries.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { soil ->
                            val isSelected = selectedSoilType == soil
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedSoilType = soil }
                                    .then(
                                        if (isSelected) Modifier.border(2.dp, Secondary, RoundedCornerShape(12.dp))
                                        else Modifier.border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                                    ),
                                color = if (isSelected) SecondaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(soilEmoji[soil] ?: "🟫", style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.width(8.dp))
                                    Text(soil.displayName, style = MaterialTheme.typography.labelMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Secondary else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Irrigation", style = MaterialTheme.typography.labelLarge, color = Primary)
            Spacer(Modifier.height(4.dp))
            Text("Choose the primary water source for this farm", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            val irrigationOptions = listOf("Canal + Tubewell", "Tubewell", "Canal", "Drip", "Rainfed")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                irrigationOptions.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { irrigation ->
                            FilterChip(
                                selected = selectedIrrigationType == irrigation,
                                onClick = { selectedIrrigationType = irrigation },
                                label = { Text(irrigation, maxLines = 1) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Previous Crop
            Text("Previous Crop", style = MaterialTheme.typography.labelLarge, color = Primary)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = previousCropText,
                onValueChange = { previousCropText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Last cultivated crop") },
                leadingIcon = { Text("🌾") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text("Water Availability", style = MaterialTheme.typography.labelLarge, color = Primary)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WaterLevel.entries.forEach { level ->
                    FilterChip(
                        selected = selectedWaterAvailability == level,
                        onClick = { selectedWaterAvailability = level },
                        label = { Text(level.displayName) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Next Planning Season", style = MaterialTheme.typography.labelLarge, color = Primary)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Season.entries.forEach { season ->
                    FilterChip(
                        selected = selectedSeason == season,
                        onClick = { selectedSeason = season },
                        label = { Text(season.displayName) }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Farming Goal
            Text("Farming Goal", style = MaterialTheme.typography.labelLarge, color = Primary)
            Spacer(Modifier.height(4.dp))
            Text("What matters most to you this season?", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))

            val goalEmoji = mapOf(FarmingGoal.BALANCED to "⚖️", FarmingGoal.MAX_PROFIT to "💰", FarmingGoal.LOW_RISK to "🛡️", FarmingGoal.QUICK_INCOME to "⏱️", FarmingGoal.LOW_WATER to "💧", FarmingGoal.SOIL_HEALTH to "🌿")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FarmingGoal.entries.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { goal ->
                            val isSelected = selectedGoal == goal
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedGoal = goal }
                                    .then(
                                        if (isSelected) Modifier.border(2.dp, Secondary, RoundedCornerShape(12.dp))
                                        else Modifier.border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                                    ),
                                color = if (isSelected) SecondaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(goalEmoji[goal] ?: "⚖️", style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.width(8.dp))
                                    Text(goal.displayName, style = MaterialTheme.typography.labelSmall, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Secondary else MaterialTheme.colorScheme.onSurface, maxLines = 2)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Analyze Button
            Button(
                onClick = {
                    val area = parsedArea ?: return@Button
                    viewModel.updateFarm(
                        farm.copy(
                            farmerName = farmerNameText.trim(),
                            location = locationText.trim(),
                            areaAcres = area,
                            soilType = selectedSoilType,
                            irrigationType = selectedIrrigationType,
                            waterAvailability = selectedWaterAvailability,
                            previousCrop = previousCropText.trim(),
                            farmingGoal = selectedGoal,
                            targetSeason = selectedSeason
                        )
                    )
                    onAnalyzeFarm()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = areaIsValid && locationIsValid && farmerNameIsValid,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Analyze My Farm", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.width(8.dp))
                Text("→")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
